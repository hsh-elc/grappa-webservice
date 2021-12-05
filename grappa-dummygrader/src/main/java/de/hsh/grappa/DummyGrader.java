package de.hsh.grappa;

import de.hsh.grappa.common.BackendPlugin;
import de.hsh.grappa.common.MimeType;
import de.hsh.grappa.common.ResponseResource;
import de.hsh.grappa.common.SubmissionResource;
import de.hsh.grappa.common.util.proforma.ProformaAttachedEmbeddedFileInfo;
import de.hsh.grappa.common.util.proforma.ProformaConverter;
import de.hsh.grappa.common.util.proforma.ProformaResponseHelper;
import de.hsh.grappa.common.util.proforma.ProformaSubmissionHelper;
import de.hsh.grappa.common.util.proforma.ProformaVersion;
import de.hsh.grappa.common.util.proforma.ResponseLive;
import de.hsh.grappa.common.util.proforma.SubmissionLive;
import de.hsh.grappa.util.IOUtils;
import de.hsh.grappa.util.Strings;
import proforma.ProformaSubmissionZipPathes;
import proforma.xml.AbstractResponseType;
import proforma.xml.AbstractSubmissionType;

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
        ProformaSubmissionHelper psh = ProformaVersion.getSubmissionHelper();
        ProformaResponseHelper prh = ProformaVersion.getResponseHelper();
        
        SubmissionLive submissionLive= new SubmissionLive(submissionResource);
        AbstractSubmissionType subm= submissionLive.getSubmission(psh.getPojoType());
        
        StringBuilder feedback = new StringBuilder("<h4>This is dummy feedback from the dummy grader</h4>");
        feedback.append("Local time: ").append(LocalDateTime.now());
        
        // As feedback we give a description of the received submission.
        // To test this grader, you could try the task in src/main/resources/task.zip
        
        double score = 0.0; // the default
        
        if (psh.hasSubmissionFiles(subm)) {
            feedback.append("<p><b>Submission files</b></p>\n");
            feedback.append("<ul>");
        	for (ProformaAttachedEmbeddedFileInfo fi : psh.getSubmissionFiles(subm, submissionLive.getZipContent())) {
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
        } else if (psh.hasExternalSubmission(subm)) {
            feedback.append("<p><b>external submission</b></p>\n");
            String uri = psh.getExternalSubmissionUri(subm);
            feedback.append("<p>uri = ").append(uri).append("</p>");
        } else {
            throw new IllegalArgumentException("Neither files nor external submission found in submission");
        }
        
        AbstractResponseType response = prh.createMergedTestFeedbackResponse(feedback.toString(), BigDecimal.valueOf(score), psh.getSubmissionId(subm), this.getClass().getName());
        
        ResponseLive responseLive = new ResponseLive(response, null, MimeType.XML);
        return responseLive.toResource(prh.getPojoType());
        
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