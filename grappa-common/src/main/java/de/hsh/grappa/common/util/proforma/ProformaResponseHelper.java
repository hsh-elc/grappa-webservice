package de.hsh.grappa.common.util.proforma;

import java.math.BigDecimal;

import de.hsh.grappa.common.ResponseResource;
import de.hsh.grappa.common.SubmissionResource;
import de.hsh.grappa.common.TaskBoundary;
import proforma.xml.AbstractResponseType;


public abstract class ProformaResponseHelper extends ProformaPojoHelper {

    public enum Audience {
        TEACHER_ONLY,
        BOTH
    }

    public abstract ResponseResource createInternalErrorResponse(String errorMessage, SubmissionResource subm, TaskBoundary tb, Audience audience);

     
    @Deprecated
    public abstract ResponseResource createInternalErrorResponse(String errorMessage, SubmissionResource subm, TaskBoundary tb, Audience audience, @Deprecated boolean isExpectedInternalErrorTypeAlwaysMergedTestFeedback);

    
    public abstract AbstractResponseType createMergedTestFeedbackResponse(String feedbackString, BigDecimal score, String submissionId, String graderEngineName);

    
	static {
		tryRegister("2.1", ProformaResponseHelper.class, "de.hsh.grappa.proforma21.Proforma21ResponseHelper");
	}
	

    public static ProformaResponseHelper getInstance(String proformaVersion) {
    	return ProformaHelper.getInstance(proformaVersion, ProformaResponseHelper.class);
    }
    
}
