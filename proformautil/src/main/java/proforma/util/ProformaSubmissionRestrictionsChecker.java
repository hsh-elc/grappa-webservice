package proforma.util;

import proforma.util.boundary.TaskBoundary;

public abstract class ProformaSubmissionRestrictionsChecker {

    protected final SubmissionLive submissionLive;
    protected final TaskBoundary tb;

    protected ProformaSubmissionRestrictionsChecker(SubmissionLive submission, TaskBoundary tb) {
        this.submissionLive = submission;
        this.tb = tb;
    }

    public abstract ProformaSubmissionRestrictionViolations checkSubmissionRestrictions() throws Exception;

}