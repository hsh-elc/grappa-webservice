package proforma.util;

import java.util.List;

public abstract class ProformaSubmissionRestrictionViolations {

    protected List<ProformaSubmissionRestrictionViolation> violations;
    protected String restrictionsDescription;

    protected ProformaSubmissionRestrictionViolations(List<ProformaSubmissionRestrictionViolation> violations, String restrictionsDescription) {
        this.violations = violations;
        this.restrictionsDescription = restrictionsDescription;
    }

    public List<ProformaSubmissionRestrictionViolation> getViolations() {
        return this.violations;
    }

    public String getRestrictionsDescription() {
        return this.restrictionsDescription;
    }

    public abstract String buildRestrictionsDescriptionHtml();

}