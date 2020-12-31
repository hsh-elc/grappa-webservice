package de.hsh.grappa.cache;

import de.hsh.grappa.proforma.SubmissionResource;

/**
 * This QueuedSubmission class is used to save
 * incoming and to-be-processed submissions in a
 * cache store, such as redis.
 */
public class QueuedSubmission {
    private final String gradeProcId;
    private final SubmissionResource subm;

    public QueuedSubmission(String gradeProcId, SubmissionResource subm) {
        this.gradeProcId = gradeProcId;
        this.subm = subm;
    }

    public String getGradeProcId() {
        return gradeProcId;
    }

    public SubmissionResource getSubmission() {
        return subm;
    }
}
