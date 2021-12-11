package de.hsh.grappa.proforma21;

import java.util.ArrayList;
import java.util.List;

import de.hsh.grappa.common.TaskBoundary;
import de.hsh.grappa.common.util.proforma.ProformaVersion;
import de.hsh.grappa.common.util.proforma.SubmissionLive;
import de.hsh.grappa.common.util.proforma.impl.ProformaSubmissionHelper;
import de.hsh.grappa.common.util.proforma.impl.ProformaSubmissionTaskHandle;
import de.hsh.grappa.util.Zip.ZipContent;
import proforma.xml.AbstractSubmissionType;
import proforma.xml21.ExternalSubmissionType;
import proforma.xml21.FeedbackLevelType;
import proforma.xml21.LmsType;
import proforma.xml21.ResultSpecType;
import proforma.xml21.SubmissionFileType;
import proforma.xml21.SubmissionFilesType;
import proforma.xml21.SubmissionType;

public class Proforma21SubmissionHelper extends ProformaSubmissionHelper {

	public Proforma21SubmissionHelper(ProformaVersion pv) {
		super(pv);
	}


	@SuppressWarnings("unchecked")
	@Override
	public Class<? extends AbstractSubmissionType> getPojoType() {
		return SubmissionType.class;
	}
	

	@Override
	public ProformaSubmissionTaskHandle getSubmissionTaskHandle(SubmissionLive submission, TaskBoundary tb) {
		return new Proforma21SubmissionTaskHandle(submission, tb);
	}


	@Override
	public String getExternalSubmissionUri(AbstractSubmissionType submission) {
    	SubmissionType s = (SubmissionType) submission;
    	ExternalSubmissionType es = s.getExternalSubmission();
    	if (es == null) return null;
    	return es.getUri();
	}


	@Override
	public boolean hasSubmissionFiles(AbstractSubmissionType submission) {
    	SubmissionType s = (SubmissionType) submission;
    	return s.getFiles() != null && !s.getFiles().getFile().isEmpty();
	}


	@Override
	public String getSubmissionId(AbstractSubmissionType submission) {
    	SubmissionType s = (SubmissionType) submission;
		return s.getId();
	}

	@Override
	public boolean hasExternalSubmission(AbstractSubmissionType submission) {
    	SubmissionType s = (SubmissionType) submission;
    	return s.getExternalSubmission() != null;
	}


	@Override
	public void setResultSpecDetailsIfEmpty(AbstractSubmissionType submission, String structure, String format,
			String studentFeedbackLevel, String teacherFeedbackLevel) {
		SubmissionType s = (SubmissionType) submission;
		ResultSpecType rs= s.getResultSpec();
        if (rs.getStructure() == null || rs.getStructure().isEmpty()) {
            rs.setStructure(structure);
        }
        if (rs.getStudentFeedbackLevel() == null) {
            rs.setStudentFeedbackLevel(FeedbackLevelType.fromValue(studentFeedbackLevel));
        }
        if (rs.getTeacherFeedbackLevel() == null) {
            rs.setTeacherFeedbackLevel(FeedbackLevelType.fromValue(teacherFeedbackLevel));
        }
        if (rs.getFormat() == null || rs.getFormat().isEmpty()) {
            rs.setFormat(format);
        }
	}

	
	public void addObjectsToLmsAnyNamespace(AbstractSubmissionType submission, Object ... objects) {
		SubmissionType s = (SubmissionType) submission;
		LmsType lms= s.getLms();
        if (lms == null) {
            lms= new LmsType();
            s.setLms(lms);
        }
        for (Object o : objects) {
        	s.getLms().getAny().add(o);
        }
	}
	
	public String[] getAllFeedbackLevels() {
		String[] result = new String[FeedbackLevelType.values().length];
		for (int i=0; i<result.length; i++) {
			result[i] = FeedbackLevelType.values()[i].value();
		}
		return result;
	}

	private Proforma21SubmissionFileHandle getSubmissionFileHandle(SubmissionFileType file, ZipContent zipContent) {
		if (file == null) return null;
		return new Proforma21SubmissionFileHandle(file, zipContent);
	}
		
	@Override
	public List<Proforma21SubmissionFileHandle> getSubmissionFileHandles(AbstractSubmissionType submission, ZipContent zipContent) {
        SubmissionType s = (SubmissionType) submission;
		SubmissionFilesType sf = s.getFiles();
		if (sf == null) return null;
		ArrayList<Proforma21SubmissionFileHandle> list= new ArrayList<>();
		for (SubmissionFileType file : sf.getFile()) {
			list.add(getSubmissionFileHandle(file, zipContent));
		}
		return list;
	}
	
}
