package de.hsh.grappa.cache;

import de.hsh.grappa.proforma.ProformaSubmission;

public class QueuedSubmission {
    private final String gradeProcId;
    private final ProformaSubmission subm;

    public QueuedSubmission(String gradeProcId, ProformaSubmission subm) {
        this.gradeProcId = gradeProcId;
        this.subm = subm;
    }

    public String getGradeProcId() {
        return gradeProcId;
    }

    public ProformaSubmission getSubmission() {
        return subm;
    }
}
