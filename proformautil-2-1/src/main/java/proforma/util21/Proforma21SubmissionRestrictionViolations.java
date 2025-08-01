package proforma.util21;

import java.util.List;

import proforma.util.ProformaSubmissionRestrictionViolation;
import proforma.util.ProformaSubmissionRestrictionViolations;

public class Proforma21SubmissionRestrictionViolations extends ProformaSubmissionRestrictionViolations {

    public Proforma21SubmissionRestrictionViolations(List<ProformaSubmissionRestrictionViolation> violations, String restrictionsDescription) {
        super(violations, restrictionsDescription);
    }

    @Override
    public String buildRestrictionsDescriptionHtml() {
        if (null != this.restrictionsDescription) {
            return "<p>" + this.restrictionsDescription + "</p>";
        } else {
            return null;
        }
    }

}