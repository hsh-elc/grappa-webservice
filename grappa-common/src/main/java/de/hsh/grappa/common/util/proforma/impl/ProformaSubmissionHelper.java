package de.hsh.grappa.common.util.proforma.impl;

import java.util.List;

import de.hsh.grappa.common.TaskBoundary;
import de.hsh.grappa.common.util.proforma.ProformaVersion;
import de.hsh.grappa.common.util.proforma.SubmissionLive;
import de.hsh.grappa.util.Zip.ZipContent;
import proforma.xml.AbstractSubmissionType;

public abstract class ProformaSubmissionHelper extends ProformaHelper {
	
	public ProformaSubmissionHelper(ProformaVersion pv) {
		super(pv);
	}

	public abstract String getSubmissionId(AbstractSubmissionType submission);

	public abstract ProformaSubmissionTaskHandle getSubmissionTaskHandle(SubmissionLive submission, TaskBoundary tb);
	

	public abstract boolean hasSubmissionFiles(AbstractSubmissionType submission);
	public abstract List<? extends ProformaSubmissionFileHandle> getSubmissionFileHandles(AbstractSubmissionType submission, ZipContent zipContent);
	public abstract boolean hasExternalSubmission(AbstractSubmissionType submission);
	public abstract String getExternalSubmissionUri(AbstractSubmissionType submission);
	
	
	
	public abstract void setResultSpecDetailsIfEmpty(AbstractSubmissionType submission, String structure, String format,
			String studentFeedbackLevel, String teacherFeedbackLevel);
	public abstract String[] getAllFeedbackLevels();
	
	public abstract void addObjectsToLmsAnyNamespace(AbstractSubmissionType submission, Object ... objects);
	
	
}
