package de.hsh.grappa.backendplugin.dummygrader;

import proforma.util.ProformaAttachedTxtFileHandle;
import proforma.util.ProformaLiveObject;
import proforma.util.ProformaSubmissionFileHandle;
import proforma.util.ProformaSubmissionSubmissionHandle;
import proforma.util.ProformaSubmissionTaskHandle;
import proforma.util.ProformaSubmissionZipPathes;
import proforma.util.ResponseLive;
import proforma.util.SubmissionLive;
import proforma.util.TaskLive;
import proforma.util.boundary.ResourceDownloader.Resource;
import proforma.util.div.FilenameUtils;
import proforma.util.div.StringEscapeUtils;
import proforma.util.div.Strings;
import proforma.util.div.XmlUtils.MarshalOption;
import proforma.util.div.Zip.ZipContent;
import proforma.util.div.Zip.ZipContentElement;
import proforma.util.resource.MimeType;
import proforma.util.resource.ResponseResource;
import proforma.util.resource.SubmissionResource;
import proforma.xml.AbstractResponseType;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Properties;
import java.util.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import de.hsh.grappa.backendplugin.BackendPlugin;

/**
 * The dummy grader process the submission and grades a score based on the content of the submitted
 * file named "score.txt", while the ".txt"-extension is optional.
 */
public class DummyGrader extends BackendPlugin {

    private static final Logger log = LoggerFactory.getLogger(DummyGrader.class);

    static {
        String lvl = System.getProperty("logging.level");
        if (!Strings.isNullOrEmpty(lvl)) {
            Level level = Level.toLevel(lvl);
            ((ch.qos.logback.classic.Logger) log).setLevel(level);
        }
    }

    @Override
    public void init(Properties properties) throws Exception {
    }
    
    private void printFile(StringBuilder feedback, byte[] binContent, String txtContent, String filename) {
        feedback.append("  filename: ").append(filename).append("<br>\n");
        if (binContent != null) {
            feedback.append("  binary content: len = ").append(binContent.length).append("<br>\n");
            String base64 = Base64.getEncoder().encodeToString(binContent);
            feedback.append("<span style='color: #000; font-family: monospace; white-space: pre-wrap; overflow-wrap: break-word; '>");
            if (base64.length() > 100) {
                feedback.append(base64.subSequence(0, 100)).append("...");
            } else {
                feedback.append(base64);
            }
            feedback.append("</span><br>\n");
        }
        if (txtContent != null) {
            feedback.append("  text content:<br>\n");
            feedback.append("<pre>\n");
            try (Scanner sc = new Scanner(txtContent)) {
                while (sc.hasNextLine()) {
                    feedback.append(StringEscapeUtils.escapeHtml4(sc.nextLine())).append("\n");
                }
            }
            feedback.append("</pre>\n");
        }
    }
    
    private void printZipContent(StringBuilder feedback, ProformaLiveObject<?,?> o) throws Exception {
        if (MimeType.ZIP.equals(o.getMimeType())) {
            feedback.append("<p>This is a zip resource</p>\n");
            feedback.append("<ul>\n");
            for (ZipContentElement elem : o.getZipContent().values()) {
                feedback.append("<li>").append(elem.getPath())
                    .append(" (").append(elem.getSize()).append(" bytes)</li>\n");
            }
            feedback.append("</ul>\n");
        } else if (MimeType.XML.equals(o.getMimeType())) {
            feedback.append("<p>This is a xml resource</p>\n");
        }
    }
    
    private String readAttachedTxt(ZipContent zipContent, String path, ProformaAttachedTxtFileHandle atfh) throws UnsupportedEncodingException {
        ZipContentElement elem = zipContent.get(path);
        if (elem == null) {
            throw new IllegalArgumentException("the path '" + path + "' does not exist in the zip contents.");
        }
        String encoding = atfh.getEncoding();
        if (Strings.isNullOrEmpty(encoding)) {
            // TODO: guess encoding from language and content.
            encoding = StandardCharsets.UTF_8.name();
        }
        String txtContent = new String(elem.getBytes(), encoding);
        return txtContent;
    }

    private byte[] readAttachedBin(ZipContent zipContent, String path) {
        ZipContentElement elem = zipContent.get(path);
        if (elem == null) {
            throw new IllegalArgumentException("the path '" + path + "' does not exist in the zip contents.");
        }
        return elem.getBytes();
    }
    
    
    
    private void processSubmission(StringBuilder feedback, SubmissionLive submissionLive, double[] inOutScore) throws UnsupportedEncodingException, MalformedURLException, IOException, Exception  {
        ProformaSubmissionSubmissionHandle pssh = submissionLive.getSubmissionSubmissionHandle(getBoundary());
        if (pssh.hasSubmissionFiles()) {
            feedback.append("<p><b>Submission files</b></p>\n");
            feedback.append("<ul>");
            for (ProformaSubmissionFileHandle fi : pssh.submissionFilesHandle().getSubmissionFileHandles()) {
                feedback.append("<li>id: ").append(fi.getId()).append("<br>\n");
                feedback.append("  mimetype: ").append(fi.getMimetype()).append("<br>\n");
                String filename = null;
                
                byte[] binContent = null;
                String txtContent = null;
                
                if (fi.embeddedBinFileHandle().get() != null) {
                    filename = fi.embeddedBinFileHandle().getFilename();
                    binContent = fi.embeddedBinFileHandle().getContent();
                }
                if (fi.embeddedTxtFileHandle().get() != null) {
                    filename = fi.embeddedTxtFileHandle().getFilename();
                    txtContent = fi.embeddedTxtFileHandle().getContent();
                }
                if (fi.attachedBinFileHandle().get() != null) {
                    filename = fi.attachedBinFileHandle().getPath();
                    String path = fi.getPathPrefixInsideZip() + fi.attachedBinFileHandle().getPath();
                    binContent = readAttachedBin(submissionLive.getZipContent(), path);
                }
                if (fi.attachedTxtFileHandle().get() != null) {
                    filename = fi.attachedTxtFileHandle().getPath();
                    String path = fi.getPathPrefixInsideZip() + fi.attachedTxtFileHandle().getPath();
                    txtContent = readAttachedTxt(submissionLive.getZipContent(), path, fi.attachedTxtFileHandle());
                }
                printFile(feedback, binContent, txtContent, filename);
                
                if ("score".equals(FilenameUtils.getBasename(filename))) {
                    if (txtContent != null) {
                        try {
                            inOutScore[0] = Double.parseDouble(txtContent);
                        } catch (Exception e) {
                            feedback.append("<b>Parse double error</b><br>\n");
                        }
                    } else {
                        feedback.append("<b>Unexpected binary file type when reading score file</b><br>\n");
                    }
                }
                feedback.append("</li>");
            }
            feedback.append("</ul>");
        } else if (pssh.hasExternalSubmission()) {
            feedback.append("<p><b>external submission</b></p>\n");
            String uri = pssh.externalSubmissionHandle().getUri();
            feedback.append("<p>uri = ").append(uri).append("</p>");
            
            Resource res = pssh.externalSubmissionHandle().download();
            String filename = res.getFileNameOrDefault();
            byte[] binContent = null;
            String txtContent = null;
            if (res.isTextContent()) {
                txtContent = new String(res.getContent(), res.getEncodingOrUtf8AsDefault());
            } else {
                binContent = res.getContent();
            }
            feedback.append("<p>Details of the downloaded file:<br>\n");
            feedback.append("  mimetype: ").append(res.getContentType()).append("<br>\n");
            feedback.append("  encoding: ").append(res.getContentEncoding()).append("<br>\n");
            printFile(feedback, binContent, txtContent, filename);
            feedback.append("</p>\n");
        } else {
            throw new IllegalArgumentException("Neither files nor external submission found in submission");
        }
        feedback.append("<p><b>Submission resource</b></p>\n");
        printZipContent(feedback, submissionLive);
    }
    
    
    @Override
    public ResponseResource grade(SubmissionResource submissionResource) throws Exception {
        SubmissionLive submissionLive= new SubmissionLive(submissionResource);
        
        StringBuilder feedback = new StringBuilder("<h4>This is dummy feedback from the dummy grader</h4>");
        feedback.append("Local time: ").append(LocalDateTime.now());
        
        // As feedback we give a description of the received submission.
        // To test this grader, you could try the task in src/main/resources/task.zip
        
        double[] score = { 0.0 }; // the default. Array because of call by reference

        processSubmission(feedback, submissionLive, score);

        ProformaSubmissionSubmissionHandle pssh = submissionLive.getSubmissionSubmissionHandle(getBoundary());
        if (pssh.unzipToEmbedded(0)) {
            feedback.append("<p>After unzipping the single submitted zip file, the submission looks like this...</p>\n");
            processSubmission(feedback, submissionLive, score);
        }

        
        TaskLive taskLive = submissionLive.getTask(getBoundary());
        String taskUuid = taskLive.getTaskUuid();
        feedback.append("<p><b>Task</b></p>\n");
        feedback.append("<p>UUID = ").append(taskUuid).append("</p>\n");
        
        ProformaSubmissionTaskHandle sth = submissionLive.getSubmissionTaskHandle(getBoundary());
        if (sth.externalTaskHandle().get() != null) {
            feedback.append("<p>External task</p>\n<ul><li>\n");
            feedback.append("URI=").append(sth.externalTaskHandle().getUri()).append("<br>\n");
            feedback.append("UUID=").append(sth.externalTaskHandle().getUuid()).append("<br>\n");
            feedback.append("</li></ul>\n");
        } else if (sth.includedTaskFileHandle() != null) {
            feedback.append("<p>Included task file</p>\n<ul><li>\n");
            byte[] binContent = null;
            String txtContent = null;
            String filename = null;
            if (sth.includedTaskFileHandle().attachedXmlFileHandle().get() != null) {
                filename = ProformaSubmissionZipPathes.TASK_DIRECTORY + "/" + sth.includedTaskFileHandle().attachedXmlFileHandle().getPath();
                txtContent = readAttachedTxt(submissionLive.getZipContent(), filename, sth.includedTaskFileHandle().attachedXmlFileHandle());
            } else if (sth.includedTaskFileHandle().attachedZipFileHandle().get() != null) {
                filename = ProformaSubmissionZipPathes.TASK_DIRECTORY + "/" + sth.includedTaskFileHandle().attachedZipFileHandle().getPath();
                binContent = readAttachedBin(submissionLive.getZipContent(), filename);
            } else if (sth.includedTaskFileHandle().embeddedXmlFileHandle().get() != null) {
                filename = sth.includedTaskFileHandle().embeddedXmlFileHandle().getFilename();
                binContent = sth.includedTaskFileHandle().embeddedXmlFileHandle().getContent();
            } else if (sth.includedTaskFileHandle().embeddedZipFileHandle().get() != null) {
                filename = sth.includedTaskFileHandle().embeddedZipFileHandle().getFilename();
                binContent = sth.includedTaskFileHandle().embeddedZipFileHandle().getContent();
            }
            printFile(feedback, binContent, txtContent, filename);
            feedback.append("</li></ul>\n");
        } else if (sth.childElementTaskHandle().get() != null) {
            feedback.append("<p>Native task child element</p>\n<ul><li>\n");
            feedback.append("UUID=").append(sth.childElementTaskHandle().getUuid()).append("<br>\n");
            feedback.append("</li></ul>\n");
        }
        feedback.append("<p><b>Task resource</b></p>\n");
        printZipContent(feedback, taskLive);
        
        AbstractResponseType response = submissionLive.getProformaVersion().getResponseHelper()
                .createMergedTestFeedbackResponse(
                        feedback.toString(), 
                        BigDecimal.valueOf(score[0]),
                        submissionLive.getSubmissionId(),
                        this.getClass().getName());
        
        ResponseLive responseLive = new ResponseLive(response, null, MimeType.XML, MarshalOption.of(MarshalOption.CDATA));
        return responseLive.getResource();
        
        // old version
        //return oldGrade(submissionResource);
    }
    
//    private ResponseResource oldGrade(SubmissionResource submissionResource) throws Exception {
//        TimeUnit.SECONDS.sleep(ThreadLocalRandom.current().nextInt(2, 6));
//        AbstractSubmissionType submPojo = ProformaConverter.convertToPojo(submissionResource);
//        return new ResponseResource(IOUtils.toByteArray(createResponse()), MimeType.ZIP);
//    }
//    
//    private InputStream createResponse() {
//        return DummyGrader.class.getResourceAsStream("/oldresponse.zip");
//    }
}