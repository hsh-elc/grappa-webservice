package de.hsh.grappa.common.util.proforma.impl;

import de.hsh.grappa.common.SubmissionBoundary;
import de.hsh.grappa.common.TaskBoundary;
import de.hsh.grappa.common.util.proforma.ProformaVersion;
import de.hsh.grappa.common.util.proforma.SubmissionLive;
import proforma.xml.AbstractSubmissionType;

public abstract class ProformaSubmissionHelper extends ProformaHelper {
	
	public ProformaSubmissionHelper(ProformaVersion pv) {
		super(pv);
	}

	public abstract String getSubmissionId(AbstractSubmissionType submission);

	public abstract ProformaSubmissionTaskHandle getSubmissionTaskHandle(SubmissionLive submission, TaskBoundary tb);
	public abstract ProformaSubmissionSubmissionHandle getSubmissionSubmissionHandle(SubmissionLive submission, SubmissionBoundary sb);
	
	
	public abstract void setResultSpecDetailsIfEmpty(AbstractSubmissionType submission, String structure, String format,
			String studentFeedbackLevel, String teacherFeedbackLevel);
	public abstract String[] getAllFeedbackLevels();
	
	public abstract void addObjectsToLmsAnyNamespace(AbstractSubmissionType submission, Object ... objects);
	
	
	
}
