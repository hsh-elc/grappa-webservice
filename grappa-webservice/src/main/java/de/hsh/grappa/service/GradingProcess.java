package de.hsh.grappa.service;

import de.hsh.grappa.plugins.backendplugin.BackendPlugin;
import de.hsh.grappa.proforma.ProformaResponse;
import de.hsh.grappa.proforma.ProformaSubmission;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GradingProcess extends Thread {
    private static final Logger log = LoggerFactory.getLogger(GradingProcess.class);
    private String graderId;
    private String gradeProcId;
    private BackendPlugin backendPlugin;
    private ProformaSubmission proformaSubmission;
    private ProformaResponse proformaResponseResult;

    public GradingProcess(BackendPlugin bp, ProformaSubmission proformaSubmission,
                          String graderId, String gradeProcId) {
        this.backendPlugin = bp;
        this.proformaSubmission = proformaSubmission;
        this.proformaResponseResult = null;
        this.graderId = graderId;
        this.gradeProcId = gradeProcId;
    }

    public ProformaResponse getProformaResponseResult() {
        return proformaResponseResult;
    }

    @Override
    public void run() {
        try {
            log.debug("In GradeProcess... sleeping");
            Thread.sleep(10000);
            log.debug("In GradeProcess... sleep ended");
            proformaResponseResult = backendPlugin.grade(proformaSubmission);
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
