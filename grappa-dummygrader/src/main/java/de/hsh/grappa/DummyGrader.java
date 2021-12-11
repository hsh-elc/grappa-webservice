package de.hsh.grappa;

import de.hsh.grappa.common.BackendPlugin;
import de.hsh.grappa.common.MimeType;
import de.hsh.grappa.common.ResponseResource;
import de.hsh.grappa.common.SubmissionResource;
import de.hsh.grappa.common.util.proforma.ProformaVersion;
import de.hsh.grappa.common.util.proforma.ResponseLive;
import de.hsh.grappa.common.util.proforma.SubmissionLive;
import de.hsh.grappa.common.util.proforma.impl.ProformaSubmissionFileHandle;
import de.hsh.grappa.util.Strings;
import de.hsh.grappa.util.XmlUtils.MarshalOption;
import de.hsh.grappa.util.Zip.ZipContentElement;
import proforma.xml.AbstractResponseType;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Properties;
import java.util.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;

/**
 * This dummy grader currently works with ProFormA version 2.1 only.
 *
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

    @Override
    public ResponseResource grade(SubmissionResource submissionResource) throws Exception {
        ProformaVersion pv = ProformaVersion.getDefault();
        
        SubmissionLive submissionLive= new SubmissionLive(submissionResource, pv);
        
        StringBuilder feedback = new StringBuilder("<h4>This is dummy feedback from the dummy grader</h4>");
        feedback.append("Local time: ").append(LocalDateTime.now());
        
        // As feedback we give a description of the received submission.
        // To test this grader, you could try the task in src/main/resources/task.zip
        
        double score = 0.0; // the default
        
        if (submissionLive.hasSubmissionFiles()) {
            feedback.append("<p><b>Submission files</b></p>\n");
            feedback.append("<ul>");
            for (ProformaSubmissionFileHandle fi : submissionLive.getSubmissionFileHandles()) {
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
                    ZipContentElement elem = submissionLive.getZipContent().get(path);
                    if (elem == null) {
                        throw new IllegalArgumentException("the path '" + path + "' does not exist in the zip contents.");
                    }
                    binContent = elem.getBytes();
                }
                if (fi.attachedTxtFileHandle().get() != null) {
                	filename = fi.attachedTxtFileHandle().getPath();
                    String path = fi.getPathPrefixInsideZip() + fi.attachedTxtFileHandle().getPath();
                    ZipContentElement elem = submissionLive.getZipContent().get(path);
                    if (elem == null) {
                        throw new IllegalArgumentException("the path '" + path + "' does not exist in the zip contents.");
                    }
                    String encoding = fi.attachedTxtFileHandle().getEncoding();
                    if (Strings.isNullOrEmpty(encoding)) {
                        // TODO: guess encoding from language and content.
                        encoding = StandardCharsets.UTF_8.name();
                    }
                    txtContent = new String(elem.getBytes(), encoding);
                }
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
                            feedback.append(sc.nextLine()).append("\n");
                        }
                    }
                    feedback.append("</pre>\n");
                }
                
                if ("score".equals(filename)) {
                    if (txtContent != null) {
                        try {
                            score = Double.parseDouble(txtContent);
                        } catch (Exception e) {
                            feedback.append("<b>Parse double error</b><br>\n");
                        }
                    } else {
                        feedback.append("<b>Unexpected binary file type</b><br>\n");
                    }
                }
                feedback.append("</li>");
            }
            
//            
//        	for (ProformaAttachedEmbeddedFileInfo fi : psh.getSubmissionFiles(subm, submissionLive.getZipContent())) {
//                feedback.append("<li>id: ").append(fi.getId()).append("<br>\n");
//                feedback.append("  mimetype: ").append(fi.getMimetype()).append("<br>\n");
//                feedback.append("  filename: ").append(fi.getFilename()).append("<br>\n");
//                if (fi.getBinContent() != null) {
//                    feedback.append("  binary content: len = ").append(fi.getBinContent().length).append("<br>\n");
//                    String base64 = Base64.getEncoder().encodeToString(fi.getBinContent());
//                    feedback.append("<span style='color: #000; font-family: monospace; white-space: pre-wrap; overflow-wrap: break-word; '>");
//                    if (base64.length() > 100) {
//                        feedback.append(base64.subSequence(0, 100)).append("...");
//                    } else {
//                        feedback.append(base64);
//                    }
//                    feedback.append("</span><br>\n");
//                }
//                if (fi.getTxtContent() != null) {
//                    feedback.append("  text content:<br>\n");
//                    feedback.append("<pre>\n");
//                    try (Scanner sc = new Scanner(fi.getTxtContent())) {
//                        while (sc.hasNextLine()) {
//                            feedback.append(sc.nextLine()).append("\n");
//                        }
//                    }
//                    feedback.append("</pre>\n");
//                }
//                
//                if ((ProformaSubmissionZipPathes.SUBMISSION_DIRECTORY + "/score").equals(fi.getFilename())) {
//                    if (fi.getTxtContent() != null) {
//                        try {
//                            score = Double.parseDouble(fi.getTxtContent());
//                        } catch (Exception e) {
//                            feedback.append("<b>Parse double error</b><br>\n");
//                        }
//                    } else {
//                        feedback.append("<b>Unexpected binary file type</b><br>\n");
//                    }
//                }
//                feedback.append("</li>");
//            }
            
            feedback.append("</ul>");
        } else if (submissionLive.hasExternalSubmission()) {
            feedback.append("<p><b>external submission</b></p>\n");
            String uri = submissionLive.getExternalSubmissionUri();
            feedback.append("<p>uri = ").append(uri).append("</p>");
        } else {
            throw new IllegalArgumentException("Neither files nor external submission found in submission");
        }
        
        AbstractResponseType response = pv.getResponseHelper()
        		.createMergedTestFeedbackResponse(
        				feedback.toString(), 
        				BigDecimal.valueOf(score),
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