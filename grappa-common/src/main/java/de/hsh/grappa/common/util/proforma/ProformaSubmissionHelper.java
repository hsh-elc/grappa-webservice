package de.hsh.grappa.common.util.proforma;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import de.hsh.grappa.common.TaskBoundary;
import de.hsh.grappa.common.TaskResource;
import de.hsh.grappa.util.Zip.ZipContentElement;
import proforma.xml.AbstractSubmissionType;

public abstract class ProformaSubmissionHelper extends ProformaPojoHelper {
	

	
	public abstract boolean isTaskExternal(AbstractSubmissionType submission);

	public abstract IncludedTaskVariant getTaskIncludedVariant(AbstractSubmissionType submission);
	
	public abstract boolean isTaskElement(AbstractSubmissionType submission);
	
	public abstract TaskResource createTaskFromExternal(AbstractSubmissionType submission, TaskBoundary tb) throws Exception;	
	public abstract TaskResource createTaskFromAttachedXmlFile(AbstractSubmissionType submission, Map<String, ZipContentElement> zipContent) throws Exception;
    
	public abstract TaskResource createTaskFromAttachedZipFile(AbstractSubmissionType submission, Map<String, ZipContentElement> zipContent) throws Exception;
    
	public abstract TaskResource createTaskFromEmbeddedXmlFile(AbstractSubmissionType submission) throws Exception;    
	public abstract TaskResource createTaskFromEmbeddedZipFile(AbstractSubmissionType submission) throws Exception;

	public abstract boolean hasSubmissionFiles(AbstractSubmissionType submission);
	public abstract boolean hasExternalSubmission(AbstractSubmissionType submission)
	;
	public abstract List<ProformaAttachedEmbeddedFileInfo> getSubmissionFiles(AbstractSubmissionType submission, Map<String, ZipContentElement> zipContent) throws UnsupportedEncodingException;

	public abstract String getExternalSubmissionUri(AbstractSubmissionType submission);
	
	public abstract String getSubmissionId(AbstractSubmissionType submission);
	
	public abstract void setResultSpecDetailsIfEmpty(AbstractSubmissionType submission, String structure, String format,
			String studentFeedbackLevel, String teacherFeedbackLevel);
	
	public abstract void addObjectsToLmsAnyNamespace(AbstractSubmissionType submission, Object ... objects);
	
	public abstract String[] getAllFeedbackLevels();
	
	static {
		tryRegister("2.1", ProformaSubmissionHelper.class, "de.hsh.grappa.proforma21.Proforma21SubmissionHelper");
	}
	

    public static ProformaSubmissionHelper getInstance(String proformaVersion) {
    	return ProformaHelper.getInstance(proformaVersion, ProformaSubmissionHelper.class);
    }

}
