package de.hsh.grappa.proforma;

import de.hsh.grappa.utils.Zip;
import org.apache.commons.io.FilenameUtils;
import proforma.ProformaSubmissionZipPathes;
import proforma.xml.AttachedTxtFileType;
import proforma.xml.IncludedTaskFileType;
import proforma.xml.SubmissionType;

import java.nio.charset.StandardCharsets;

/**
 * Retrieves an attached task XML file from within a submission ZIP.
 */
public class AttachedXmlTaskExtractor extends TaskExtractor {
    private SubmissionType concreteSubmPojo;
    private SubmissionWrapper submissionWrapper;
    private TaskWrapper taskWrapper;

    public AttachedXmlTaskExtractor(SubmissionType concreteSubmPojo,
                                    SubmissionWrapper submissionWrapper) {
        this.concreteSubmPojo = concreteSubmPojo;
        // An attached xml task file comes only in a zipped submission
        if (!submissionWrapper.getProformasubmissionResource().getMimeType().equals(MimeType.ZIP))
            throw new IllegalArgumentException("ProformaSubmission is not a ZIP archive.");
        this.submissionWrapper = submissionWrapper;
    }

    @Override
    public TaskWrapper getTask() throws Exception {
        if (null != taskWrapper)
            return taskWrapper;

        IncludedTaskFileType included = concreteSubmPojo.getIncludedTaskFile();
        if (null == included)
            throw new IllegalArgumentException();

        AttachedTxtFileType a = included.getAttachedXmlFile();
        String filePath = a.getValue();
        String taskXmlFilePath = FilenameUtils.concat(ProformaSubmissionZipPathes.TASK_DIRECTORY, filePath);
        String taskXmlFileContent =
            Zip.getTextFileContentFromZip(submissionWrapper.getProformasubmissionResource().getContent(), taskXmlFilePath,
            StandardCharsets.UTF_8);
        // The task uuid doesn't need to be extracted here. Besides, that attribute is optional anyway.
        // The uuid will be retrieved from the task xml file by the ProformaTask subclass instead.
        //String taskUuid = included.getUuid();
        //if (Strings.isNullOrEmpty(taskUuid))
        //    throw new NotImplementedException("taskuuid needs to be extracted from the task xml file");
        byte[] taskXmlFileBytes = taskXmlFileContent.getBytes(StandardCharsets.UTF_8);
        taskWrapper = new TaskWrapperImpl(new TaskResource(taskXmlFileBytes, MimeType.XML));
        return taskWrapper;
    }
}
