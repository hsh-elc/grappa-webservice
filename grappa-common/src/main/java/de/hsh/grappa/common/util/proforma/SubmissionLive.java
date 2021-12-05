package de.hsh.grappa.common.util.proforma;

import java.util.Map;

import de.hsh.grappa.common.MimeType;
import de.hsh.grappa.common.SubmissionResource;
import de.hsh.grappa.common.TaskBoundary;
import de.hsh.grappa.common.TaskResource;
import de.hsh.grappa.util.Zip.ZipContentElement;
import proforma.ProformaSubmissionZipPathes;
import proforma.xml.AbstractSubmissionType;

/**
 * <p>Helper class to represent a ProFormA submission in memory. This could be either a representation of
 * a XML or a ZIP file. </p>
 * 
 * <p>Usage:</p>
 * <pre>
 * SubmissionResource resource= ...;
 * SubmissionLive live= new SubmissionLive(resource);
 * ProformaSubmissionHelper ph = ...;  // e. g. ... = ProformaVersion.getSubmissionHelper();
 * AbstractSubmissionType pojo= live.getSubmission(ph.getPojoType());
 * SubmissionResource res = live.getResource(); // get the original resource
 * // make some changes in the data of pojo.
 * pojo.set... ;
 * // create a new resource from the changes
 * SubmissionResource newResource= live.toResource(ph.getPojoType(), &lt;more-grader-specific-JAXB-classes&gt;);
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
    public <T extends AbstractSubmissionType> T getSubmission(Class<T> clazz) throws Exception {
        return super.getPojo(clazz);
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
    public SubmissionResource toResource(Class<? extends AbstractSubmissionType> pojoType, Class<?> ... contextClasses) throws Exception {
        return (SubmissionResource) super.toResource(pojoType, contextClasses);
    }

    
    @Override
    protected Class<SubmissionResource> getResourceType() {
        return SubmissionResource.class;
    }

    @Override
    protected String getMainXmlFileName() {
        return ProformaSubmissionZipPathes.SUBMISSION_XML_FILE_NAME;
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
    public TaskResource getTask(TaskBoundary tb, ProformaSubmissionHelper sh) throws Exception {
        if (task != null) return task;
        
        AbstractSubmissionType s = getPojo(sh.getPojoType());
        if (sh.isTaskExternal(s)) {
            task = sh.createTaskFromExternal(s, tb);
        } else if (sh.isTaskElement(s)) {
            throw new UnsupportedOperationException("TODO: implement native xml task element");
        } else switch (sh.getTaskIncludedVariant(s)) {
			case ATTACHED_XML:
				task = sh.createTaskFromAttachedXmlFile(s, getZipContent());
				break;
			case ATTACHED_ZIP:
				task = sh.createTaskFromAttachedZipFile(s, getZipContent());
				break;
			case EMBEDDED_XML:
				task = sh.createTaskFromEmbeddedXmlFile(s);
				break;
			case EMBEDDED_ZIP:
				task = sh.createTaskFromEmbeddedZipFile(s);
				break;
			case NONE:
				break;
			default:
	            throw new IllegalArgumentException("Unknown task element in submission");        
        }
        
        return task;
    }
    
}
