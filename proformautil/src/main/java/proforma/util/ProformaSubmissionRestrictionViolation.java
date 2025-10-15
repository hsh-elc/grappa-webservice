package proforma.util;

public abstract class ProformaSubmissionRestrictionViolation {
    
    protected String variant; // Violations with the same variant should be grouped together
    protected String objectName; // e.g. file name

    protected ProformaSubmissionRestrictionViolation(String variant, String objectName) {
        this.variant = variant;
        this.objectName = objectName;
    }

    public String getVariant() {
        return this.variant;
    }
    
    public String getObjectName() {
        return this.objectName;
    }

    public abstract String buildFeedbackHtml();

}