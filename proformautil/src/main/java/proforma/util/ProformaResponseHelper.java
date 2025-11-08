package proforma.util;

import proforma.util.boundary.TaskBoundary;
import proforma.util.div.Zip.ZipContent;
import proforma.util.resource.ResponseResource;
import proforma.util.resource.SubmissionResource;
import proforma.xml.AbstractResponseType;

import java.math.BigDecimal;
import java.util.List;


public abstract class ProformaResponseHelper extends ProformaHelper {

    public ProformaResponseHelper(ProformaVersion pv) {
        super(pv);
    }


    public enum Audience {
        TEACHER_ONLY,
        BOTH
    }

    public abstract ResponseResource createInternalErrorResponse(String errorMessage, SubmissionResource subm, TaskBoundary tb, Audience audience) throws Exception;

    public abstract ResponseResource createSubmissionRestrictionViolationResponse(ProformaSubmissionRestrictionViolations violations, SubmissionResource subm, TaskBoundary tb) throws Exception;

    public abstract ResponseResource generateMergedFeedbackIfRequested(ResponseResource responseResource, SubmissionResource subm, TaskBoundary tb) throws Exception;
    

    @Deprecated
    public abstract ResponseResource createInternalErrorResponse(String errorMessage, SubmissionResource subm, TaskBoundary tb, Audience audience, @Deprecated boolean isExpectedInternalErrorTypeAlwaysMergedTestFeedback) throws Exception;

    public abstract boolean hasInternalError(ResponseResource responseResource) throws Exception;

    public abstract AbstractResponseType createMergedTestFeedbackResponse(String feedbackString, BigDecimal score, String submissionId, String graderEngineName);


    public abstract List<? extends ProformaResponseFileHandle> getResponseFileHandles(AbstractResponseType response, ZipContent zipContent);

    /**
     * File id and metadata will be auto-generated.
     */
    public abstract void addAttachedTxtFile(AbstractResponseType response, String filepath);

    /**
     * File id and metadata will be auto-generated.
     */
    public abstract void addAttachedBinFile(AbstractResponseType response, String filepath);

    /**
     * File id and metadata will be auto-generated.
     */
    public abstract void addEmbeddedTxtFile(AbstractResponseType response, String filename, String content);

    /**
     * File id and metadata will be auto-generated.
     */
    public abstract void addEmbeddedBinFile(AbstractResponseType response, String filename, byte[] content);
}
