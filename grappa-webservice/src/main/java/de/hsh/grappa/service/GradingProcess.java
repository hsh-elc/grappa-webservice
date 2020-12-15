package de.hsh.grappa.service;

import de.hsh.grappa.plugins.backendplugin.BackendPlugin;
import de.hsh.grappa.proforma.ResponseResource;
import de.hsh.grappa.proforma.SubmissionResource;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GradingProcess extends Thread {
    private static final Logger log = LoggerFactory.getLogger(GradingProcess.class);
    private String graderId;
    private String gradeProcId;
    private BackendPlugin backendPlugin;
    private SubmissionResource submissionBlob;
    private ResponseResource responseResourceResult;

    public GradingProcess(BackendPlugin bp, SubmissionResource submissionBlob,
                          String graderId, String gradeProcId) {
        this.backendPlugin = bp;
        this.submissionBlob = submissionBlob;
        this.responseResourceResult = null;
        this.graderId = graderId;
        this.gradeProcId = gradeProcId;
    }

    public ResponseResource getProformaResponseResult() {
        return responseResourceResult;
    }

    @Override
    public void run() {
        try {
            log.debug("In GradeProcess... sleeping");
            Thread.sleep(10000);
            log.debug("In GradeProcess... sleep ended");
            responseResourceResult = backendPlugin.grade(submissionBlob);
        } catch (InterruptedException e) {
            log.info("[GraderId: '{}', GradeProcessId: '{}']: Grading process interrupted.",
                graderId, gradeProcId);
        } catch (Exception e) {
            log.info("[GraderId: '{}', GradeProcessId: '{}']: Grading process failed with error: {}",
                graderId, gradeProcId, e.getMessage());
            log.error(ExceptionUtils.getStackTrace(e));
        }
    }
}
