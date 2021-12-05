package de.hsh.grappa.util.proforma;

import java.util.Map;

import de.hsh.grappa.common.MimeType;
import de.hsh.grappa.common.TaskResource;
import de.hsh.grappa.util.Zip.ZipContentElement;
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
 * AbstractTaskType pojo= live.getTask();
 * TaskResource res = live.getResource(); // get the original resource
 * // make some changes in the data of pojo.
 * pojo.set... ;
 * // create a new resource from the changes
 * TaskResource newResource= live.toResource(&lt;grader-specific-JAXB-classes&gt;);
 * live.getResource(); // returns the new resource
 * </pre>
 */
public class TaskLive extends ProformaLiveObject<TaskResource, AbstractTaskType>{

    /**
     * Creates an in-memory representation of a given task resource
     * @param resource The given resource
     * @throws Exception
     */
    public TaskLive(TaskResource resource) throws Exception {
        super(resource);
    }
    
    /**
     * Creates an object from an in-memory representation and augments this with additional
     * data to be written to a ZIP or XML file when serialized.
     * @param mimeType must be ZIP, if {@code otherZipContentExceptMainXmlFile} is not empty.
     */
    public TaskLive(AbstractTaskType task, Map<String, ZipContentElement> otherZipContentExceptMainXmlFile, MimeType mimeType, Class<?> ... contextClasses) throws Exception {
        super(task, otherZipContentExceptMainXmlFile, mimeType, contextClasses);
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
     * on by calling {@link #toResource()}.
     * @throws Exception
     */
    public AbstractTaskType getTask() throws Exception {
        return (AbstractTaskType) super.getPojo();
    }
    
    /**
     * @return pojo as described by {@link #getTask()}, but cast to a subclass.
     * @throws UnsupportedOperationException if the cast fails
     */
    public <T extends AbstractTaskType> T getTaskAs(Class<T> clazz) throws Exception {
        return super.getPojoAs(clazz);
    }


    
    /**
     * @return the original resource or the new resource created by {@link #toResource(Class...)}.
     */
    @Override public TaskResource getResource() {
        return (TaskResource)super.getResource();
    }
    

    /**
     * Create a new resource object from the live data.
     * @param contextClasses classes needed when marshalling XML
     * @return a new resource object
     * @throws Exception
     */
    @Override
    public TaskResource toResource(Class<?> ... contextClasses) throws Exception {
        return (TaskResource) super.toResource(contextClasses);
    }

    
    @Override
    protected Class<TaskResource> getResourceType() {
        return TaskResource.class;
    }

    @Override
    protected Class<AbstractTaskType> getPojoType() {
        return AbstractTaskType.class;
    }

    @Override
    protected String getMainXmlFileName() {
        return ProformaTaskZipPathes.TASK_XML_FILE_NAME;
    }

    
}