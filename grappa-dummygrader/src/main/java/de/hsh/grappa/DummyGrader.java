package de.hsh.grappa;

import de.hsh.grappa.common.BackendPlugin;
import de.hsh.grappa.common.MimeType;
import de.hsh.grappa.common.ResponseResource;
import de.hsh.grappa.common.SubmissionResource;
import de.hsh.grappa.util.IOUtils;
import de.hsh.grappa.util.Strings;
import de.hsh.grappa.util.proforma.ProformaConverter;
import de.hsh.grappa.util.proforma.ResponseLive;
import de.hsh.grappa.util.proforma.ProformaLiveObject.FileInfo;
import de.hsh.grappa.util.proforma.SubmissionLive;
import proforma.ProformaSubmissionZipPathes;
import proforma.xml.AbstractSubmissionType;
import proforma.xml.ExternalSubmissionType;
import proforma.xml.GraderEngineType;
import proforma.xml.MergedTestFeedbackType;
import proforma.xml.OverallResultType;
import proforma.xml.ResponseFilesType;
import proforma.xml.ResponseMetaDataType;
import proforma.xml.ResponseType;
import proforma.xml.SubmissionFileType;
import proforma.xml.SubmissionFilesType;
import proforma.xml.SubmissionType;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;

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
        
        SubmissionLive submissionLive= new SubmissionLive(submissionResource);
        SubmissionType subm= submissionLive.getSubmissionAs(SubmissionType.class);
        
        StringBuilder feedback = new StringBuilder("<h4>This is dummy feedback from the dummy grader</h4>");
        feedback.append("Local time: ").append(LocalDateTime.now());
        
        // As feedback we give a description of the received submission.
        // To test this grader, you could try the task in src/main/resources/task.zip
        
        double score = 0.0; // the default
        SubmissionFilesType files = subm.getFiles();
        ExternalSubmissionType es = subm.getExternalSubmission();
        if (files != null) {
            feedback.append("<p><b>Submission files</b></p>\n");
            feedback.append("<ul>");
            for (SubmissionFileType sf : files.getFile()) {
                FileInfo fi = submissionLive.getFromFileChoiceGroup(sf.getId(), sf.getMimetype(),
                       sf.getEmbeddedBinFile(), sf.getEmbeddedTxtFile(), sf.getAttachedBinFile(), sf.getAttachedTxtFile(),
                       ProformaSubmissionZipPathes.SUBMISSION_DIRECTORY);
                feedback.append("<li>id: ").append(fi.getId()).append("<br>\n");
                feedback.append("  mimetype: ").append(fi.getMimetype()).append("<br>\n");
                feedback.append("  filename: ").append(fi.getFilename()).append("<br>\n");
                if (fi.getBinContent() != null) {
                    feedback.append("  binary content: len = ").append(fi.getBinContent().length).append("<br>\n");
                    String base64 = Base64.getEncoder().encodeToString(fi.getBinContent());
                    feedback.append("<span style='color: #000; font-family: monospace; white-space: pre-wrap; overflow-wrap: break-word; '>");
                    if (base64.length() > 100) {
                        feedback.append(base64.subSequence(0, 100)).append("...");
                    } else {
                        feedback.append(base64);
                    }
                    feedback.append("</span><br>\n");
                }
                if (fi.getTxtContent() != null) {
                    feedback.append("  text content:<br>\n");
                    feedback.append("<pre>\n");
                    try (Scanner sc = new Scanner(fi.getTxtContent())) {
                        while (sc.hasNextLine()) {
                            feedback.append(sc.nextLine()).append("\n");
                        }
                    }
                    feedback.append("</pre>\n");
                }
                
                if ((ProformaSubmissionZipPathes.SUBMISSION_DIRECTORY + "/score").equals(fi.getFilename())) {
                    if (fi.getTxtContent() != null) {
                        try {
                            score = Double.parseDouble(fi.getTxtContent());
                        } catch (Exception e) {
                            feedback.append("<b>Parse double error</b><br>\n");
                        }
                    } else {
                        feedback.append("<b>Unexpected binary file type</b><br>\n");
                    }
                }
                feedback.append("</li>");
            }
            feedback.append("</ul>");
        } else if (es != null) {
            feedback.append("<p><b>external submission</b></p>\n");
            String uri = es.getUri();
            feedback.append("<p>uri = ").append(uri).append("</p>");
        } else {
            throw new IllegalArgumentException("Neither files nor external submission found in submission");
        }
        
        String feedbackString= feedback.toString();
        MergedTestFeedbackType mtf = new MergedTestFeedbackType();
        mtf.setStudentFeedback(feedbackString);
        mtf.setTeacherFeedback(feedbackString);
        OverallResultType or = new OverallResultType();
        or.setScore(BigDecimal.valueOf(score));
        mtf.setOverallResult(or);

        ResponseType response = new ResponseType();
        response.setFiles(new ResponseFilesType()); // empty, but required
        response.setMergedTestFeedback(mtf);
        response.setSubmissionId(subm.getId());
        ResponseMetaDataType meta = new ResponseMetaDataType();
        GraderEngineType ge = new GraderEngineType();
        ge.setName(this.getClass().getName());
        meta.setGraderEngine(ge);
        response.setResponseMetaData(meta);
        
        ResponseLive responseLive = new ResponseLive(response, null, MimeType.XML);
        return responseLive.toResource();
        
        // old version
        //return oldGrade(submissionResource);
    }
    
    private ResponseResource oldGrade(SubmissionResource submissionResource) throws Exception {
        TimeUnit.SECONDS.sleep(ThreadLocalRandom.current().nextInt(2, 6));
        AbstractSubmissionType submPojo = ProformaConverter.convertToPojo(submissionResource);
        return new ResponseResource(IOUtils.toByteArray(createResponse()), MimeType.ZIP);
    }
    
    private InputStream createResponse() {
        return DummyGrader.class.getResourceAsStream("/oldresponse.zip");
    }
}