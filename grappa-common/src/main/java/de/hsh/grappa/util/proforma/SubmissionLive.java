package de.hsh.grappa.util.proforma;

import java.util.Map;

import de.hsh.grappa.common.MimeType;
import de.hsh.grappa.common.SubmissionResource;
import de.hsh.grappa.common.TaskBoundary;
import de.hsh.grappa.common.TaskResource;
import de.hsh.grappa.exceptions.GrappaException;
import de.hsh.grappa.exceptions.NotFoundException;
import de.hsh.grappa.util.Strings;
import de.hsh.grappa.util.Zip.ZipContentElement;
import proforma.ProformaSubmissionZipPathes;
import proforma.xml.AbstractSubmissionType;
import proforma.xml.AttachedTxtFileType;
import proforma.xml.EmbeddedBinFileType;
import proforma.xml.ExternalTaskType;
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
 * AbstractSubmissionType pojo= live.getSubmission();
 * SubmissionResource res = live.getResource(); // get the original resource
 * // make some changes in the data of pojo.
 * pojo.set... ;
 * // create a new resource from the changes
 * SubmissionResource newResource= live.toResource(&lt;grader-specific-JAXB-classes&gt;);
 * live.getResource(); // returns the new resource
 * </pre>
 */
public class SubmissionLive extends ProformaLiveObject<SubmissionResource, AbstractSubmissionType>{

    private TaskResource task;
    
    
    /**
     * Creates an in-memory representation of a given submission resource
     * @param submissionResource The given resource
     * @throws Exception
     */
    public SubmissionLive(SubmissionResource submissionResource) throws Exception {
        super(submissionResource);
    }
    
    
    /**
     * Creates an object from an in-memory representation and augments this with additional
     * data to be written to a ZIP or XML file when serialized.
     * @param mimeType must be ZIP, if {@code otherZipContentExceptMainXmlFile} is not empty.
     */
    public SubmissionLive(AbstractSubmissionType subm, Map<String, ZipContentElement> otherZipContentExceptMainXmlFile, MimeType mimeType, Class<?> ... contextClasses) throws Exception {
        super(subm, otherZipContentExceptMainXmlFile, mimeType, contextClasses);
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
    public AbstractSubmissionType getSubmission() throws Exception {
        return (AbstractSubmissionType) super.getPojo();
    }
    
    /**
     * @return pojo as described by {@link #getSubmission()}, but cast to a subclass.
     * @throws UnsupportedOperationException if the cast fails
     */
    public <T extends AbstractSubmissionType> T getSubmissionAs(Class<T> clazz) throws Exception {
        return super.getPojoAs(clazz);
    }

    /**
     * @return the original resource or the new resource created by {@link #toResource(Class...)}.
     */
    @Override public SubmissionResource getResource() {
        return (SubmissionResource)super.getResource();
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
    protected Class<AbstractSubmissionType> getPojoType() {
        return AbstractSubmissionType.class;
    }

    @Override
    protected String getMainXmlFileName() {
        return ProformaSubmissionZipPathes.SUBMISSION_XML_FILE_NAME;
    }

    private SubmissionType getProforma21Submission() throws Exception {
        return getSubmissionAs(SubmissionType.class);
    }
    
    /**
     * Retrieves the task as part of the submission.
     * This will trigger a {@link #getSubmission()} call.
     * Currently supported are:
     * <ul>
     *   <li>Supported: ProFormA 2.1</li>
     *   <li>Unsupported: any other ProFormA version</li>
     *   <li>Supported: attached xml and zip task files</li>
     *   <li>Supported: external tasks retrievable from a local cache or from the internet</li>
     *   <li>Supported: embedded task zip file and embedded task xml file</li>
     *   <li>Unsupported: native task element</li>
     * </ul>
     * @return The task as a resource
     * @throws Exception
     */
    public TaskResource getTask(TaskBoundary tb) throws Exception {
        if (task != null) return task;
        
        SubmissionType s = getProforma21Submission();
        if (null != s.getExternalTask()) {
            task = createTaskFromExternal(tb);
            //throw new UnsupportedOperationException("external-task not yet supported");
        } else if (null != s.getIncludedTaskFile()) {
            IncludedTaskFileType included= s.getIncludedTaskFile();
            if (null != included.getAttachedXmlFile()) {
                task = createTaskFromAttachedXmlFile();
            } else if (null != included.getAttachedZipFile()) {
                task = createTaskFromAttachedZipFile();
            } else if (null != included.getEmbeddedZipFile()) {
                task = createTaskFromEmbeddedZipFile();
            } else if (null != included.getEmbeddedXmlFile()) {
                task = createTaskFromEmbeddedXmlFile();
            } else
                throw new IllegalArgumentException("Unknown IncludedTaskFileType");
        } else if (null != s.getTask()) {
            throw new UnsupportedOperationException("TODO: implement native xml task element");
        } else {
            throw new IllegalArgumentException("Unknown task element in submission");        
        }
        
        return task;
    }
    
    private TaskResource createTaskFromExternal(TaskBoundary tb) throws Exception {
        SubmissionType s = getProforma21Submission();
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
                throw new GrappaException(String.format("Downloading external task resource failed: %s",
                    taskRepoUrl), e);
            }
        }
        
    }
    
    private TaskResource createTaskFromAttachedXmlFile() throws Exception {
        AttachedTxtFileType a= getProforma21Submission().getIncludedTaskFile().getAttachedXmlFile();
        String filePath = a.getValue();
        return createTaskFromAttachedFile(filePath, MimeType.XML);
    }
    
    private TaskResource createTaskFromAttachedZipFile() throws Exception {
        String filePath= getProforma21Submission().getIncludedTaskFile().getAttachedZipFile();
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
    
    private TaskResource createTaskFromEmbeddedXmlFile() throws Exception {
        EmbeddedBinFileType e = getProforma21Submission().getIncludedTaskFile().getEmbeddedXmlFile();
        byte[] bytes = e.getValue();
        return new TaskResource(bytes, MimeType.XML);
    }
    
    private TaskResource createTaskFromEmbeddedZipFile() throws Exception {
        EmbeddedBinFileType e = getProforma21Submission().getIncludedTaskFile().getEmbeddedZipFile();
        byte[] bytes = e.getValue();
        return new TaskResource(bytes, MimeType.ZIP);
    }
    
    
}
