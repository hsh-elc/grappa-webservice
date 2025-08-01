package proforma.util;

public abstract class ProformaSubmissionRestrictionsChecker {

    protected final SubmissionLive submissionLive;

    protected ProformaSubmissionRestrictionsChecker(SubmissionLive submission) {
        this.submissionLive = submission;
    }

    public abstract ProformaSubmissionRestrictionViolations checkSubmissionRestrictions() throws Exception;

}