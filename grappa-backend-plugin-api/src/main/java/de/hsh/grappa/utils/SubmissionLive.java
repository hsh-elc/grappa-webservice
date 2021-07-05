package de.hsh.grappa.utils;

import de.hsh.grappa.proforma.MimeType;
import de.hsh.grappa.proforma.SubmissionResource;
import de.hsh.grappa.proforma.TaskResource;
import de.hsh.grappa.utils.Zip.ZipContentElement;
import proforma.ProformaSubmissionZipPathes;
import proforma.xml.AttachedTxtFileType;
import proforma.xml.IncludedTaskFileType;
import proforma.xml.SubmissionType;

/**
 * <p>Helper class to represent a ProFormA submission in memory. This could be either a representation of
 * a XML or a ZIP file. </p>
 * 
 * <p>Usage:</p>
 * <pre>
 * SubmissionResource resource= ...;
 * SubmissionLive live= new SubmissionLive(resource);
 * SubmissionType pojo= live.getSubmission();
 * // make some changes in the data of pojo.
 * pojo.set... ;
 * SubmissionResource newResource= live.toResource(&lt;grader-specific-JAXB-classes&gt;);
 * </pre>
 */
public class SubmissionLive extends ProformaLiveObject<SubmissionResource, SubmissionType>{

    /**
     * Creates an in-memory representation of a given submission resource
     * @param submissionResource The given resource
     * @throws Exception
     */
    public SubmissionLive(SubmissionResource submissionResource) throws Exception {
        super(submissionResource);
    }
    
    /**
     * @return the string "submission"
     */
    @Override
    public String displayName() {
        return "submission";
    }

    /**
     * @return a pojo deserialized from the submission.xml file. This pojo can be modified and stored later
     * on by calling {@link #toResource()}.
     * @throws Exception
     */
    public SubmissionType getSubmission() throws Exception {
        return (SubmissionType) super.getPojo();
    }
    
    /**
     * Create a new resource object from the live data.
     * @param contextClasses classes needed when marshalling XML
     * @return a new resource object
     * @throws Exception
     */
    @Override
    public SubmissionResource toResource(Class<?> ... contextClasses) throws Exception {
        return (SubmissionResource) super.toResource(contextClasses);
    }

    
    @Override
    protected Class<SubmissionResource> getResourceType() {
        return SubmissionResource.class;
    }

    @Override
    protected Class<SubmissionType> getPojoType() {
        return SubmissionType.class;
    }

    @Override
    protected String getMainXmlFileName() {
        return ProformaSubmissionZipPathes.SUBMISSION_XML_FILE_NAME;
    }

    /**
     * Retrieves the task as part of the submission.
     * This will trigger a {@link #getSubmission()} call.
     * Currently only attached xml and zip task files are supported.
     * @return The task as a resource
     * @throws Exception
     */
    public TaskResource getTask() throws Exception {
    	if (null != getSubmission().getExternalTask()) {
    		throw new UnsupportedOperationException("external-task not yet supported");
    	} else if (null != getSubmission().getIncludedTaskFile()) {
    		IncludedTaskFileType included= getSubmission().getIncludedTaskFile();
            if (null != included.getAttachedXmlFile()) {
                return createTaskFromAttachedXmlFile();
            } else if (null != included.getAttachedZipFile()) {
                return createTaskFromAttachedZipFile();
            } else if (null != included.getEmbeddedZipFile()) {
                throw new UnsupportedOperationException("embedded-zip-file not yet supported");
            } else if (null != included.getEmbeddedXmlFile()) {
                // See the latest addition to the Proforma format v2.1:
                // https://github.com/ProFormA/proformaxml/blob/master/Whitepaper.md#722-the-included-task-file-element
                // TODO: This case should handle a bare-bone task XML encoded as base64,
                // without the leading XML preamble.
                throw new UnsupportedOperationException("embedded-xml-file element not yet supported");
            } else
                throw new IllegalArgumentException("Unknown IncludedTaskFileType");
        } else if (null != getSubmission().getTask())
            throw new UnsupportedOperationException("TODO: implement native xml task element");
        else throw new IllegalArgumentException("Unknown task element in submission");    	
    }
    
    private TaskResource createTaskFromAttachedXmlFile() throws Exception {
    	AttachedTxtFileType a= getSubmission().getIncludedTaskFile().getAttachedXmlFile();
        String filePath = a.getValue();
    	return createTaskFromAttachedFile(filePath, MimeType.XML);
    }
    
    private TaskResource createTaskFromAttachedZipFile() throws Exception {
    	String filePath= getSubmission().getIncludedTaskFile().getAttachedZipFile();
    	return createTaskFromAttachedFile(filePath, MimeType.ZIP);
    }
    
    
    private TaskResource createTaskFromAttachedFile(String filePath, MimeType mimeType) throws Exception {
        String taskFilePath = ProformaSubmissionZipPathes.TASK_DIRECTORY + "/" + filePath;
        ZipContentElement task= getZipContentElement(taskFilePath);
        if (task == null) {
        	throw new IllegalArgumentException("There is no file '"+taskFilePath+"' inside the ProFormA submission");
        }
        return new TaskResource(task.getBytes(), mimeType);
    }
    
}
