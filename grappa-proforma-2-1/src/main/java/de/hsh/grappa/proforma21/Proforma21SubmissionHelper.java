package de.hsh.grappa.proforma21;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.hsh.grappa.common.MimeType;
import de.hsh.grappa.common.TaskBoundary;
import de.hsh.grappa.common.TaskResource;
import de.hsh.grappa.common.util.proforma.IncludedTaskVariant;
import de.hsh.grappa.common.util.proforma.ProformaAttachedEmbeddedFileInfo;
import de.hsh.grappa.common.util.proforma.ProformaSubmissionHelper;
import de.hsh.grappa.exceptions.NotFoundException;
import de.hsh.grappa.util.Strings;
import de.hsh.grappa.util.Zip.ZipContentElement;
import proforma.ProformaSubmissionZipPathes;
import proforma.xml.AbstractSubmissionType;
import proforma.xml21.AttachedTxtFileType;
import proforma.xml21.EmbeddedBinFileType;
import proforma.xml21.ExternalSubmissionType;
import proforma.xml21.ExternalTaskType;
import proforma.xml21.FeedbackLevelType;
import proforma.xml21.IncludedTaskFileType;
import proforma.xml21.LmsType;
import proforma.xml21.ResultSpecType;
import proforma.xml21.SubmissionFileType;
import proforma.xml21.SubmissionFilesType;
import proforma.xml21.SubmissionType;

public class Proforma21SubmissionHelper extends ProformaSubmissionHelper {

	@SuppressWarnings("unchecked")
	@Override
	public Class<? extends AbstractSubmissionType> getPojoType() {
		return SubmissionType.class;
	}

	@Override
	public List<ProformaAttachedEmbeddedFileInfo> getSubmissionFiles(AbstractSubmissionType submission, Map<String, ZipContentElement> zipContent) throws UnsupportedEncodingException {
        SubmissionType s = (SubmissionType) submission;
		SubmissionFilesType sf = s.getFiles();
		if (sf == null) return null;
		
		ArrayList<ProformaAttachedEmbeddedFileInfo> result = new ArrayList<>();
		for (SubmissionFileType f : sf.getFile()) {
            ProformaAttachedEmbeddedFileInfo fi = new Proforma21FileChoiceGroupHelper().
                	getFromFileChoiceGroup(f.getId(), f.getMimetype(),
                       f.getEmbeddedBinFile(), f.getEmbeddedTxtFile(), 
                       f.getAttachedBinFile(), f.getAttachedTxtFile(),
                       ProformaSubmissionZipPathes.SUBMISSION_DIRECTORY, zipContent);
            result.add(fi);
		}
		return result;
	}
	
	
	@Override
	public boolean isTaskExternal(AbstractSubmissionType submission) {
        SubmissionType s = (SubmissionType) submission;
        return s.getExternalTask() != null;
	}
	
	@Override
	public IncludedTaskVariant getTaskIncludedVariant(AbstractSubmissionType submission) {
        SubmissionType s = (SubmissionType) submission;
        IncludedTaskFileType included = s.getIncludedTaskFile();
        if (included == null) {
        	return IncludedTaskVariant.NONE;
        }
        if (null != included.getAttachedXmlFile()) {
            return IncludedTaskVariant.ATTACHED_XML;
        } else if (null != included.getAttachedZipFile()) {
            return IncludedTaskVariant.ATTACHED_ZIP;
        } else if (null != included.getEmbeddedZipFile()) {
           return IncludedTaskVariant.EMBEDDED_ZIP;
        } else if (null != included.getEmbeddedXmlFile()) {
            return IncludedTaskVariant.EMBEDDED_XML;
        } else {
        	throw new IllegalArgumentException("Unknown IncludedTaskFileType");
        }
	}
	
	@Override
	public boolean isTaskElement(AbstractSubmissionType submission) {
        SubmissionType s = (SubmissionType) submission;
        return s.getTask() != null;
	}
	
	@Override
	public TaskResource createTaskFromExternal(AbstractSubmissionType submission, TaskBoundary tb) throws Exception {
        SubmissionType s = (SubmissionType) submission;
        ExternalTaskType et = s.getExternalTask();
        
        String taskUuid = et.getUuid();
        String taskRepoUrl = et.getUri();
        if (Strings.isNullOrEmpty(taskRepoUrl)) {
            if (Strings.isNullOrEmpty(taskUuid))
                throw new Exception("Neither the task repository url nor the task uuid have been " +
                    "specified.");

            // If the task repo url is empty and the taskuuid is set, try getting the task from cache
            try {
                return tb.getCachedTask(taskUuid);
            } catch (NotFoundException e) {
                throw new NotFoundException(String.format("The task uuid '%s' specified in the external task element " +
                    "(with the task repo url being empty) is not cached by the middleware.", taskUuid), e);
            }
        } else {
            try {
                return tb.downloadTask(taskRepoUrl);
            } catch (Exception e) {
                throw new Exception(String.format("Downloading external task resource failed: %s",
                    taskRepoUrl), e);
            }
        }
        
    }
	
	
	
	@Override
    public TaskResource createTaskFromAttachedXmlFile(AbstractSubmissionType submission, Map<String, ZipContentElement> zipContent) throws Exception {
    	SubmissionType s = (SubmissionType) submission;
        AttachedTxtFileType a= s.getIncludedTaskFile().getAttachedXmlFile();
        String filePath = a.getValue();
        return createTaskFromAttachedFile(filePath, MimeType.XML, zipContent);
    }
    
	@Override
    public TaskResource createTaskFromAttachedZipFile(AbstractSubmissionType submission, Map<String, ZipContentElement> zipContent) throws Exception {
    	SubmissionType s = (SubmissionType) submission;
        String filePath= s.getIncludedTaskFile().getAttachedZipFile();
        return createTaskFromAttachedFile(filePath, MimeType.ZIP, zipContent);
    }
    
    
    private TaskResource createTaskFromAttachedFile(String filePath, MimeType mimeType, Map<String, ZipContentElement> zipContent) throws Exception {
        String taskFilePath = ProformaSubmissionZipPathes.TASK_DIRECTORY + "/" + filePath;
        ZipContentElement task= zipContent.get(taskFilePath);
        if (task == null) {
            throw new IllegalArgumentException("There is no file '"+taskFilePath+"' inside the ProFormA submission");
        }
        return new TaskResource(task.getBytes(), mimeType);
    }
    
    
	@Override
    public TaskResource createTaskFromEmbeddedXmlFile(AbstractSubmissionType submission) throws Exception {
    	SubmissionType s = (SubmissionType) submission;
        EmbeddedBinFileType e = s.getIncludedTaskFile().getEmbeddedXmlFile();
        byte[] bytes = e.getValue();
        return new TaskResource(bytes, MimeType.XML);
    }
    
	@Override
    public TaskResource createTaskFromEmbeddedZipFile(AbstractSubmissionType submission) throws Exception {
    	SubmissionType s = (SubmissionType) submission;
        EmbeddedBinFileType e = s.getIncludedTaskFile().getEmbeddedZipFile();
        byte[] bytes = e.getValue();
        return new TaskResource(bytes, MimeType.ZIP);
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


	
}
