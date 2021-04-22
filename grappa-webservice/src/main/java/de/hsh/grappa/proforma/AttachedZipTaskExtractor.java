package de.hsh.grappa.proforma;

import de.hsh.grappa.utils.Zip;
import org.apache.commons.io.FilenameUtils;
import proforma.ProformaSubmissionZipPathes;
import proforma.xml.IncludedTaskFileType;

import java.io.ByteArrayInputStream;

/**
 * Retrieves an attached task ZIP file from a submission.
 */
public class AttachedZipTaskExtractor extends TaskExtractor {
    private proforma.xml.SubmissionType concreteSubmPojo;
    private SubmissionWrapper submissionWrapper;
    private TaskWrapper taskWrapper;

    public AttachedZipTaskExtractor(proforma.xml.SubmissionType concreteSubmPojo,
                                    SubmissionWrapper submissionWrapper) {
        this.concreteSubmPojo = concreteSubmPojo;
        // An attached zip task file comes only in a zipped submission
        if(!submissionWrapper.getProformasubmissionResource().getMimeType().equals(MimeType.ZIP))
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
        byte[] taskZipFileBytes = Zip.getFileFromZip(submissionWrapper.getProformasubmissionResource().getContent(),
            taskZipPath);
        ByteArrayInputStream taskZipFileInputStream = new ByteArrayInputStream(taskZipFileBytes);

        byte[] taskBytes = Zip.getFileFromZip(taskZipFileInputStream, ProformaSubmissionZipPathes.TASK_XML_FILE_NAME);
        //String taskXml = new String(taskXmlBytes, StandardCharsets.UTF_8);

        // MimeType.XML, because the task.xml has already been retrieved
        // as byte[] from the ZIP archive
        taskWrapper = new TaskWrapperImpl(new TaskResource(taskBytes, MimeType.XML));
        return taskWrapper;
    }
}
