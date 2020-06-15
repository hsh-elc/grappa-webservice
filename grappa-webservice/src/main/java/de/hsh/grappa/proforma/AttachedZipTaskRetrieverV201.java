package de.hsh.grappa.proforma;

import de.hsh.grappa.proformaxml.v201.IncludedTaskFileType;
import de.hsh.grappa.utils.Zip;
import org.apache.commons.io.FilenameUtils;

import java.io.ByteArrayInputStream;

/**
 * Retrievs an attached ZIP task file from the submission archive
 */
public class AttachedZipTaskRetrieverV201 extends TaskRetriever {
    private de.hsh.grappa.proformaxml.v201.SubmissionType concreteSubmPojo;
    private SubmissionInternals submissionInternals;
    private TaskInternals taskInternals;

    public AttachedZipTaskRetrieverV201(de.hsh.grappa.proformaxml.v201.SubmissionType concreteSubmPojo,
                                        SubmissionInternals submissionInternals) {
        this.concreteSubmPojo = concreteSubmPojo;
        // An attached zip task file comes only in a zipped submission
        if(!submissionInternals.getProformaSubmission().getMimeType().equals(MimeType.ZIP))
            throw new IllegalArgumentException("ProformaSubmission is not a ZIP archive.");
        this.submissionInternals = submissionInternals;
    }

    @Override
    public TaskInternals getTask() throws Exception {
        if(null != taskInternals)
            return taskInternals;

        IncludedTaskFileType included = concreteSubmPojo.getIncludedTaskFile();
        if(null == included)
            throw new IllegalArgumentException();

        String filePath = included.getAttachedZipFile();
        String taskZipPath = FilenameUtils.concat(ProformaSubmissionZipPathes.TASK_DIRECTORY, filePath);
        byte[] taskZipFileBytes = Zip.getFileFromZip(submissionInternals.getProformaSubmission().getContent(),
            taskZipPath);
        ByteArrayInputStream taskZipFileInputStream = new ByteArrayInputStream(taskZipFileBytes);
        // TODO: this code needs to go somewhere else (ZipTask/ZipTaskStream?)
        // or do an initial extraction somewhere and cache the task stream
        // once. That way I'll also have something real to return in getTaskStreamType
        //final String taskXmlFilePathWithinTaskZip = "task.xml";
        // Within the task.zip, the task.xml file is in the root directory, i.e. /task.xml
        byte[] taskBytes = Zip.getFileFromZip(taskZipFileInputStream, ProformaSubmissionZipPathes.TASK_XML_FILE_NAME);
        //String taskXml = new String(taskXmlBytes, StandardCharsets.UTF_8);

        // TODO: remove. Retrieving the taskUuid is already covered in ProformaTaskExtendedV201
//        XmlTask xmlTask = new XmlTask(new InputStreamCopy(new ByteArrayInputStream(taskBytes)));
//        AbstractTaskType abstractTask = xmlTask.getTaskPojo();
//        //assert !(abstractTask instanceof de.hsh.grappa.proformaxml.v201.TaskType) : "wrong TaskType version";
//        TaskType taskPojo = (TaskType) abstractTask;
//        TaskV201 t = new TaskV201(taskPojo);
//        String taskuuid = t.getUuid();

        // MimeType.XML, because the task.xml has already been retrieved
        // as byte[] from the ZIP archive
        taskInternals = new TaskInternalsV201(new ProformaTask(taskBytes, MimeType.XML));
        return taskInternals;
    }
}
