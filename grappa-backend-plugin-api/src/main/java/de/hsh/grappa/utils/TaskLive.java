package de.hsh.grappa.utils;

import de.hsh.grappa.proforma.TaskResource;
import proforma.ProformaTaskZipPathes;
import proforma.xml.TaskType;

/**
 * <p>Helper class to represent a ProFormA task in memory. This could be either a representation of
 * a XML or a ZIP file. </p>
 * 
 * <p>Usage:</p>
 * <pre>
 * TaskResource resource= ...;
 * TaskLive live= new TaskLive(resource);
 * TaskType pojo= live.getTask();
 * // make some changes in the data of pojo.
 * pojo.set... ;
 * TaskResource newResource= live.toResource(&lt;grader-specific-JAXB-classes&gt;);
 * </pre>
 */
public class TaskLive extends ProformaLiveObject<TaskResource, TaskType>{

    /**
     * Creates an in-memory representation of a given task resource
     * @param resource The given resource
     * @throws Exception
     */
    public TaskLive(TaskResource resource) throws Exception {
        super(resource);
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
    public TaskType getSubmission() throws Exception {
        return (TaskType) super.getPojo();
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
    protected Class<TaskType> getPojoType() {
        return TaskType.class;
    }

    @Override
    protected String getMainXmlFileName() {
        return ProformaTaskZipPathes.TASK_XML_FILE_NAME;
    }

    
}
