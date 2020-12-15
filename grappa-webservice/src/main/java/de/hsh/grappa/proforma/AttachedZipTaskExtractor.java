package de.hsh.grappa.proforma;

import de.hsh.grappa.utils.Zip;
import org.apache.commons.io.FilenameUtils;
import proforma.ProformaSubmissionZipPathes;
import proforma.xml.IncludedTaskFileType;

import java.io.ByteArrayInputStream;

/**
 * Retrievs an attached ZIP task file from the submission archive
 */
public class AttachedZipTaskExtractor extends TaskExtractor {
    private proforma.xml.SubmissionType concreteSubmPojo;
    private SubmissionWrapper submissionWrapper;
    private TaskWrapper taskWrapper;

    public AttachedZipTaskExtractor(proforma.xml.SubmissionType concreteSubmPojo,
                                    SubmissionWrapper submissionWrapper) {
        this.concreteSubmPojo = concreteSubmPojo;
        // An attached zip task file comes only in a zipped submission
        if(!submissionWrapper.getProformaSubmissionBlob().getMimeType().equals(MimeType.ZIP))
            throw new IllegalArgumentException("ProformaSubmission is not a ZIP archive.");
        this.submissionWrapper = submissionWrapper;
    }

    @Override
    public TaskWrapper getTask() throws Exception {
        if(null != taskWrapper)
            return taskWrapper;

        IncludedTaskFileType included = concreteSubmPojo.getIncludedTaskFile();
        if(null == included)
            throw new IllegalArgumentException();

        String filePath = included.getAttachedZipFile();
        String taskZipPath = FilenameUtils.concat(ProformaSubmissionZipPathes.TASK_DIRECTORY, filePath);
        byte[] taskZipFileBytes = Zip.getFileFromZip(submissionWrapper.getProformaSubmissionBlob().getContent(),
            taskZipPath);
        ByteArrayInputStream taskZipFileInputStream = new ByteArrayInputStream(taskZipFileBytes);
        // TODO: this code needs to go somewhere else (ZipTask/ZipTaskStream?)
        // or do an initial extraction somewhere and cache the task stream
        // once. That way I'll also have something real to return in getTaskStreamType
        //final String taskXmlFilePathWithinTaskZip = "task.xml";
        // Within the task.zip, the task.xml file is in the root directory, i.e. /task.xml
        String taskXmlFilePath = FilenameUtils.concat(ProformaSubmissionZipPathes.TASK_DIRECTORY,
            ProformaSubmissionZipPathes.TASK_XML_FILE_NAME);
        //byte[] taskBytes = Zip.getFileFromZip(taskZipFileInputStream, ProformaSubmissionZipPathes.TASK_XML_FILE_NAME);
        byte[] taskBytes = Zip.getFileFromZip(taskZipFileInputStream, taskXmlFilePath);
        //String taskXml = new String(taskXmlBytes, StandardCharsets.UTF_8);

        // TODO: remove. Retrieving the taskUuid is already covered in ProformaTaskExtendedV201
//        XmlTask xmlTask = new XmlTask(new InputStreamCopy(new ByteArrayInputStream(taskBytes)));
//        AbstractTaskType abstractTask = xmlTask.getTaskPojo();
//        //assert !(abstractTask instanceof proforma.xml.TaskType) : "wrong TaskType version";
//        TaskType taskPojo = (TaskType) abstractTask;
//        TaskV201 t = new TaskV201(taskPojo);
//        String taskuuid = t.getUuid();

        // MimeType.XML, because the task.xml has already been retrieved
        // as byte[] from the ZIP archive
        taskWrapper = new TaskWrapperImpl(new TaskResource(taskBytes, MimeType.XML));
        return taskWrapper;
    }
}
