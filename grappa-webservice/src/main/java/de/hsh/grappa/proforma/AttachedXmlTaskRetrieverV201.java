package de.hsh.grappa.proforma;

import com.google.common.base.Charsets;
import de.hsh.grappa.proformaxml.v201.AttachedTxtFileType;
import de.hsh.grappa.proformaxml.v201.IncludedTaskFileType;
import de.hsh.grappa.proformaxml.v201.SubmissionType;
import de.hsh.grappa.utils.Zip;
import org.apache.commons.io.FilenameUtils;

import java.nio.charset.StandardCharsets;

public class AttachedXmlTaskRetrieverV201 extends TaskRetriever {
    private SubmissionType concreteSubmPojo;
    private SubmissionInternals submissionInternals;
    private TaskInternals taskInternals;

    public AttachedXmlTaskRetrieverV201(SubmissionType concreteSubmPojo,
                                        SubmissionInternals submissionInternals) {
        this.concreteSubmPojo = concreteSubmPojo;
        // An attached xml task file comes only in a zipped submission
        if (!submissionInternals.getProformaSubmission().getMimeType().equals(MimeType.ZIP))
            throw new IllegalArgumentException("ProformaSubmission is not a ZIP archive.");
        this.submissionInternals = submissionInternals;
    }

    @Override
    public TaskInternals getTask() throws Exception {
        if (null != taskInternals)
            return taskInternals;

        IncludedTaskFileType included = concreteSubmPojo.getIncludedTaskFile();
        if (null == included)
            throw new IllegalArgumentException();

        AttachedTxtFileType a = included.getAttachedXmlFile();
        String filePath = a.getValue();
        String taskXmlFilePath = FilenameUtils.concat(ProformaSubmissionZipPathes.TASK_DIRECTORY, filePath);
        String taskXmlFileContent =
            Zip.getTextFileContentFromZip(submissionInternals.getProformaSubmission().getContent(), taskXmlFilePath,
            StandardCharsets.UTF_8);
        // The task uuid doesn't need to be extracted here. Besides, that attribute is optional anyway.
        // The uuid will be retrieved from the task xml file by the ProformaTask subclass instead.
        //String taskUuid = included.getUuid();
        //if (Strings.isNullOrEmpty(taskUuid))
        //    throw new NotImplementedException("taskuuid needs to be extracted from the task xml file");
        byte[] taskXmlFileBytes = taskXmlFileContent.getBytes(Charsets.UTF_8);
        taskInternals = new TaskInternalsV201(new ProformaTask(taskXmlFileBytes, MimeType.XML));
        return taskInternals;
    }
}
