package de.hsh.grappa.cache;

import proforma.util.resource.SubmissionResource;

/**
 * This QueuedSubmission class is used to save
 * incoming and to-be-processed submissions in a
 * cache store, such as redis.
 */
public class QueuedSubmission {
    private final String gradeProcId;
    private final String lmsId;
    private final SubmissionResource subm;

    public QueuedSubmission(String gradeProcId, String lmsId, SubmissionResource subm) {
        this.gradeProcId = gradeProcId;
        this.lmsId = lmsId;
        this.subm = subm;
    }

    public String getGradeProcId() {
        return gradeProcId;
    }

    public String getLmsId() {
        return lmsId;
    }

    public SubmissionResource getSubmission() {
        return subm;
    }
}
