package de.hsh.grappa.common.util.proforma;

import java.util.List;

import de.hsh.grappa.common.MimeType;
import de.hsh.grappa.common.TaskResource;
import de.hsh.grappa.common.util.proforma.impl.ProformaTaskFileHandle;
import de.hsh.grappa.exceptions.BadRequestException;
import de.hsh.grappa.util.XmlUtils.MarshalOption;
import de.hsh.grappa.util.Zip.ZipContent;
import proforma.ProformaTaskZipPathes;
import proforma.xml.AbstractTaskType;

/**
 * <p>Helper class to represent a ProFormA task in memory. This could be either a representation of
 * a XML or a ZIP file. </p>
 * 
 * <p>Usage:</p>
 * <pre>
 * TaskResource resource= ...;
 * TaskLive live= new TaskLive(resource);
 * ProformaTaskHelper ph = ...;  // e. g. ... = ProformaVersion.getTaskHelper();
 * AbstractTaskType pojo= live.getTask(ph.getPojoType());
 * TaskResource res = live.getResource(); // get the original resource
 * // make some changes in the data of pojo.
 * pojo.set... ;
 * // create a new resource from the changes
 * TaskResource newResource= live.toResource(ph.getPojoType(), &lt;more-grader-specific-JAXB-classes&gt;);
 * live.getResource(); // returns the new resource
 * </pre>
 */
public class TaskLive extends ProformaLiveObject<TaskResource, AbstractTaskType>{

    /**
     * Creates an in-memory representation of a given task resource
     * @param resource The given resource
     * @throws Exception
     */
    public TaskLive(TaskResource resource, ProformaVersion pv, Class<?> ... contextClasses) throws Exception {
        super(resource, pv, contextClasses);
    }
    
    /**
     * Creates an object from an in-memory representation and augments this with additional
     * data to be written to a ZIP or XML file when serialized.
     * @param mimeType must be ZIP, if {@code otherZipContentExceptMainXmlFile} is not empty.
     */
    public TaskLive(AbstractTaskType task, ZipContent otherZipContentExceptMainXmlFile, MimeType mimeType, MarshalOption[] marshalOptions, Class<?> ... contextClasses) throws Exception {
        super(task, otherZipContentExceptMainXmlFile, mimeType, marshalOptions, contextClasses);
    }
    
    
    /**
     * @return the string "task"
     */
    @Override
    public String displayName() {
        return "task";
    }

    /**
     * @return a pojo deserialized from the task.xml file. This pojo can be modified and stored later
     * on by calling {@link #markPojoChanged(MarshalOption[], Class...)}.
     * @throws Exception
     */
    public <T extends AbstractTaskType> T getTask() throws Exception {
        return super.getPojo(getProformaVersion().getTaskHelper().getPojoType());
    }


    
    /**
     * @return the original resource or the new resource created by {@link #toResource(Class...)}.
     * @throws Exception 
     */
    @Override public TaskResource getResource() throws Exception {
        return (TaskResource)super.getResource();
    }
    


    
    @Override
    protected Class<TaskResource> getResourceType() {
        return TaskResource.class;
    }

    @Override
    protected String getMainXmlFileName() {
        return ProformaTaskZipPathes.TASK_XML_FILE_NAME;
    }
    
    /**
	 * @return the task's uuid
	 * @throws UnsupportedOperationException if the task is unsupported version
	 * @throws BadRequestException if taskuuid is missing in the task
	 * @throws Exception
	 */
	public String getTaskUuid() throws Exception {
		return getProformaVersion().getTaskHelper().getTaskUuid(getTask());
	}

	
	/**
	 * @return a list of handles allowing access to embedded and attached files.
	 * @throws Exception
	 */
	public List<? extends ProformaTaskFileHandle> getTaskFileHandles() throws Exception {
		return getProformaVersion().getTaskHelper().getTaskFileHandles(getTask(), getZipContent());
	}
    
}
