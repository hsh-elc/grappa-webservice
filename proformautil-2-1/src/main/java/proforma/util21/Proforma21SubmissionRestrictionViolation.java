package proforma.util21;

import java.math.BigInteger;

import proforma.util.ProformaSubmissionRestrictionViolation;

public class Proforma21SubmissionRestrictionViolation extends ProformaSubmissionRestrictionViolation {

    private boolean isMaxSizeViolation = false;

    private Proforma21SubmissionRestrictionViolation(String variant, String objectName) {
        super(variant, objectName);
    }

    private Proforma21SubmissionRestrictionViolation(String variant, String objectName, boolean isMaxSizeViolation) {
        this(variant, objectName);
        this.isMaxSizeViolation = isMaxSizeViolation;
    }

    @Override
    public String buildFeedbackHtml() {
        if (isMaxSizeViolation) {
            return "<p>" + this.variant + ": <strong>" + this.objectName + " Bytes</strong></p>";
        } else {
            return "<p>" + this.variant + ": <strong>" + this.objectName + "</strong></p>";
        }
    }

    public static Proforma21SubmissionRestrictionViolation maxSizeExceeded(BigInteger maxSize) {
        return new Proforma21SubmissionRestrictionViolation("Max. submission size exceeded", maxSize.toString(), true);
    }

    public static Proforma21SubmissionRestrictionViolation missingRequiredFile(String fileName) {
        return new Proforma21SubmissionRestrictionViolation("Missing required file", fileName);
    }

    public static Proforma21SubmissionRestrictionViolation existingProhibitedFile(String fileName) {
        return new Proforma21SubmissionRestrictionViolation("Existing prohibited file", fileName);
    }

    public static Proforma21SubmissionRestrictionViolation missingRequiredRegexMatch(String regexPattern) {
        return new Proforma21SubmissionRestrictionViolation("Missing filename that matches required regex match", regexPattern);
    }

    public static Proforma21SubmissionRestrictionViolation existingProhibitedRegexMatch(String regexPattern) {
        return new Proforma21SubmissionRestrictionViolation("Existing filename that matches prohibited regex match", regexPattern);
    }
}