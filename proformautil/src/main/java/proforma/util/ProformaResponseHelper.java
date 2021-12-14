package proforma.util;

import java.math.BigDecimal;
import java.util.List;

import proforma.util.boundary.TaskBoundary;
import proforma.util.div.Zip.ZipContent;
import proforma.util.resource.ResponseResource;
import proforma.util.resource.SubmissionResource;
import proforma.xml.AbstractResponseType;


public abstract class ProformaResponseHelper extends ProformaHelper {

    public ProformaResponseHelper(ProformaVersion pv) {
		super(pv);
	}


	public enum Audience {
        TEACHER_ONLY,
        BOTH
    }

    public abstract ResponseResource createInternalErrorResponse(String errorMessage, SubmissionResource subm, TaskBoundary tb, Audience audience) throws Exception;

     
    @Deprecated
    public abstract ResponseResource createInternalErrorResponse(String errorMessage, SubmissionResource subm, TaskBoundary tb, Audience audience, @Deprecated boolean isExpectedInternalErrorTypeAlwaysMergedTestFeedback) throws Exception;

    
    public abstract AbstractResponseType createMergedTestFeedbackResponse(String feedbackString, BigDecimal score, String submissionId, String graderEngineName);

    
	public abstract List<? extends ProformaResponseFileHandle> getResponseFileHandles(AbstractResponseType response, ZipContent zipContent);

    

}
