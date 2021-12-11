package de.hsh.grappa.common.util.proforma.impl;

import java.math.BigDecimal;
import java.util.List;

import de.hsh.grappa.common.ResponseResource;
import de.hsh.grappa.common.SubmissionResource;
import de.hsh.grappa.common.TaskBoundary;
import de.hsh.grappa.common.util.proforma.ProformaVersion;
import de.hsh.grappa.util.Zip.ZipContent;
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
