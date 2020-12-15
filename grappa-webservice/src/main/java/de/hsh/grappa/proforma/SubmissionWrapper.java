package de.hsh.grappa.proforma;

import com.google.common.base.Charsets;
import de.hsh.grappa.utils.XmlUtils;
import de.hsh.grappa.utils.Zip;
import proforma.ProformaSubmissionZipPathes;
import proforma.xml.AbstractSubmissionType;

public abstract class SubmissionWrapper {
    private SubmissionResource submissionBlob;
    private AbstractSubmissionType abstractSubmPojo;
    private TaskExtractor taskRetriever;


    public SubmissionWrapper(SubmissionResource submissionBlob) throws Exception {
        this.submissionBlob = submissionBlob;
        abstractSubmPojo = createAbstractSubmissionPojo();
        taskRetriever = createTaskExtractor();
    }

    public SubmissionResource getProformaSubmissionBlob() {
        return submissionBlob;
    }

    public void setProformaSubmission(SubmissionResource submissionBlob) {
        this.submissionBlob = submissionBlob;
    }

    public TaskWrapper getTask() throws Exception {
        return taskRetriever.getTask();
    }

    protected abstract TaskExtractor createTaskExtractor();

    protected AbstractSubmissionType getAbstractSubmPojo() {
        return abstractSubmPojo;
    }

    private AbstractSubmissionType createAbstractSubmissionPojo() throws Exception {
        // get the submission xml file bytes, unless it's a zipped submission...
        byte[] submXmlFileBytes = submissionBlob.getContent();
        if (submissionBlob.getMimeType().equals(MimeType.ZIP)) {
            String submXmlContent = Zip.getTextFileContentFromZip(submissionBlob.getContent(),
                ProformaSubmissionZipPathes.SUBMISSION_XML_FILE_NAME, Charsets.UTF_8);
            submXmlFileBytes = submXmlContent.getBytes(Charsets.UTF_8);
        }
        return XmlUtils.unmarshalToObject(submXmlFileBytes, AbstractSubmissionType.class);
    }
}
