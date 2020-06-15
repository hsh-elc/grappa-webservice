package de.hsh.grappa.proforma;

import com.google.common.base.Charsets;
import de.hsh.grappa.utils.XmlUtils;
import de.hsh.grappa.utils.Zip;

public abstract class SubmissionInternals {
    private ProformaSubmission proformaSubmission;
    private AbstractSubmissionType abstractSubmPojo;
    private TaskRetriever taskRetriever;


    public SubmissionInternals(ProformaSubmission proformaSubmission) throws Exception {
        this.proformaSubmission = proformaSubmission;
        abstractSubmPojo = createAbstractSubmissionPojo();
        taskRetriever = createTaskRetriever();
    }

    public ProformaSubmission getProformaSubmission() {
        return proformaSubmission;
    }

    public void setProformaSubmission(ProformaSubmission proformaSubmission) {
        this.proformaSubmission = proformaSubmission;
    }

    public TaskInternals getTask() throws Exception {
        return taskRetriever.getTask();
    }

    protected abstract TaskRetriever createTaskRetriever();

    protected AbstractSubmissionType getAbstractSubmPojo() {
        return abstractSubmPojo;
    }

    private AbstractSubmissionType createAbstractSubmissionPojo() throws Exception {
        // get the submission xml file bytes, unless it's a zipped submission...
        byte[] submXmlFileBytes = proformaSubmission.getContent();
        if (proformaSubmission.getMimeType().equals(MimeType.ZIP)) {
            String submXmlContent = Zip.getTextFileContentFromZip(proformaSubmission.getContent(),
                ProformaSubmissionZipPathes.SUBMISSION_XML_FILE_NAME, Charsets.UTF_8);
            submXmlFileBytes = submXmlContent.getBytes(Charsets.UTF_8);
        }
        return XmlUtils.unmarshalToObject(submXmlFileBytes, AbstractSubmissionType.class);
    }
}
