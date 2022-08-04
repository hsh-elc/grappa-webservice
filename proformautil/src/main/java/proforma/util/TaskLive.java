package proforma.util;

import proforma.util.div.XmlUtils.MarshalOption;
import proforma.util.div.Zip.ZipContent;
import proforma.util.resource.MimeType;
import proforma.util.resource.TaskResource;
import proforma.xml.AbstractTaskType;

import java.util.List;

/**
 * <p>Helper class to represent a ProFormA task in memory. This could be either a representation of
 * a XML or a ZIP file. </p>
 *
 * <p>For usage scenarios see {@link ProformaLiveObject}.</p>
 */
public class TaskLive extends ProformaLiveObject<TaskResource, AbstractTaskType> {

    /**
     * Creates an in-memory representation of a given task resource
     *
     * @param resource The given resource
     * @throws Exception
     */
    public TaskLive(TaskResource resource, Class<?>... contextClasses) throws Exception {
        super(resource, contextClasses);
    }

    /**
     * Creates an object from an in-memory representation and augments this with additional
     * data to be written to a ZIP or XML file when serialized.
     *
     * @param mimeType must be ZIP, if {@code otherZipContentExceptMainXmlFile} is not empty.
     */
    public TaskLive(AbstractTaskType task, ZipContent otherZipContentExceptMainXmlFile, MimeType mimeType, MarshalOption[] marshalOptions, Class<?>... contextClasses) throws Exception {
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
    @Override
    public TaskResource getResource() throws Exception {
        return (TaskResource) super.getResource();
    }


    @Override
    protected Class<TaskResource> getResourceType() {
        return TaskResource.class;
    }

    @Override
    public String getMainXmlFileName() {
        return ProformaTaskZipPathes.TASK_XML_FILE_NAME;
    }

    /**
     * @return the task's uuid
     * @throws UnsupportedOperationException if the task is unsupported version
     * @throws IllegalArgumentException      if taskuuid is missing in the task
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
        ZipContent zc = null;
        if (MimeType.ZIP.equals(getMimeType())) {
            zc = getZipContent();
        }
        return getProformaVersion().getTaskHelper().getTaskFileHandles(getTask(), zc);
    }

}
