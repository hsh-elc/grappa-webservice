package de.hsh.grappa.proforma;

import de.hsh.grappa.utils.XmlUtils;
import de.hsh.grappa.utils.Zip;
import proforma.ProformaSubmissionZipPathes;
import proforma.xml.AbstractTaskType;

import javax.xml.bind.annotation.XmlSchema;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * This task class represents a task object that is decoupled
 * from its owning submission (i.e. no info on whether this
 * task is external, included, or a native xml element in a
 * submission).
 */
public abstract class TaskWrapper {
    private TaskResource taskResource;
    private AbstractTaskType abstractTaskPojo;

    public TaskWrapper(TaskResource taskResource) throws Exception {
        this.taskResource = taskResource;
        abstractTaskPojo = createAbstractTaskPojo();
    }

    public TaskResource getProformaTaskBlob() {
        return taskResource;
    }

    public void setProformaTask(TaskResource taskResource) {
        this.taskResource = taskResource;
    }

    public abstract String getUuid();

    public String getUriNamespace() {
        return abstractTaskPojo.getClass().getPackage().getAnnotation(XmlSchema.class).namespace();
    }

    protected AbstractTaskType getAbstractTaskPojo() {
        return abstractTaskPojo;
    }

    private AbstractTaskType createAbstractTaskPojo() throws Exception {
        // get the task xml file bytes, unless it's a zipped task...
        byte[] taskXmlFileBytes = taskResource.getContent();
        // If this ProformaTask is of the ZIP mimetype, we need to extract
        // the task.xml file from the ZIP archive.
        // Handling the ZIP/XML mimetype cases here is a bit dirty. If
        // ProformaTask sub classes needed more than just the task.xml
        // from a task.zip, this code should be
        // refactored properly into some sort of ZipProformaTaskExtended
        // and XmlProfarmaTaskExtended.

        // TODO: This never happens to be a ZIP, since the calling code
        // already extracts the XML from the archive. That should not be
        // happening. Extraction should be happening here, so the following
        // stays.
        if (taskResource.getMimeType().equals(MimeType.ZIP)) {
            Path taskXmlFilePath = Paths.get(
                ProformaSubmissionZipPathes.TASK_DIRECTORY,
                ProformaSubmissionZipPathes.TASK_XML_FILE_NAME);
            taskXmlFileBytes = Zip.getFileFromZip(taskResource.getContent(), taskXmlFilePath.toString());
        }
        return XmlUtils.unmarshalToObject(taskXmlFileBytes, AbstractTaskType.class);
    }
}
