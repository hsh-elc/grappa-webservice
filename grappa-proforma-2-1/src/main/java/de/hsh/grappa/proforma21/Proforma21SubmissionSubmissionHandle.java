package de.hsh.grappa.proforma21;

import de.hsh.grappa.common.MimeType;
import de.hsh.grappa.common.SubmissionBoundary;
import de.hsh.grappa.common.util.proforma.SubmissionLive;
import de.hsh.grappa.common.util.proforma.impl.ProformaSubmissionSubmissionHandle;
import de.hsh.grappa.util.Zip.ZipContent;
import proforma.xml21.SubmissionType;

public class Proforma21SubmissionSubmissionHandle extends ProformaSubmissionSubmissionHandle {

	private Proforma21SubmissionFilesHandle submissionFilesHandle;
	private Proforma21ExternalSubmissionHandle externalSubmissionHandle;
	
	protected Proforma21SubmissionSubmissionHandle(SubmissionLive submission, SubmissionBoundary sb) {
		super(submission, sb);
		if (submission == null) throw new AssertionError(this.getClass() + ": submission shouldn't be null");
	}

	@Override
	public Proforma21SubmissionFilesHandle submissionFilesHandle() throws Exception {
		SubmissionType s = getSubmission().getSubmission();
		if (submissionFilesHandle == null) {
			ZipContent zc = null;
			if (MimeType.ZIP.equals(getSubmission().getMimeType())) {
				zc = getSubmission().getZipContent();
			}
			submissionFilesHandle = new Proforma21SubmissionFilesHandle(s, zc, "files");
		}
		return submissionFilesHandle;
	}

	@Override
	public Proforma21ExternalSubmissionHandle externalSubmissionHandleImpl() throws Exception {
		SubmissionType s = getSubmission().getSubmission();
		if (externalSubmissionHandle == null) {
			externalSubmissionHandle = new Proforma21ExternalSubmissionHandle(s, "externalSubmission");
		}
		return externalSubmissionHandle;
	}
    
}
