package de.hsh.grappa.proforma;

import java.nio.charset.StandardCharsets;

import de.hsh.grappa.utils.XmlUtils;
import de.hsh.grappa.utils.Zip;
import proforma.ProformaSubmissionZipPathes;
import proforma.xml.AbstractSubmissionType;

/**
 * A wrapper class for a SubmissionResource.
 * A SubmissionResource comes in form of a ZIP or a bare-bone
 * XML file. This class extracts all necessary data required
 * for further processing (caching of task information) and
 * grading.
 */
public abstract class SubmissionWrapper {
    private SubmissionResource submissionResource;
    private AbstractSubmissionType abstractSubmPojo;
    private TaskExtractor taskRetriever;


    public SubmissionWrapper(SubmissionResource submissionResource) throws Exception {
        this.submissionResource = submissionResource;
        abstractSubmPojo = createAbstractSubmissionPojo();
        taskRetriever = createTaskExtractor();
    }

    public SubmissionResource getProformasubmissionResource() {
        return submissionResource;
    }

    public void setProformaSubmission(SubmissionResource submissionResource) {
        this.submissionResource = submissionResource;
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
        byte[] submXmlFileBytes = submissionResource.getContent();
        if (submissionResource.getMimeType().equals(MimeType.ZIP)) {
            String submXmlContent = Zip.getTextFileContentFromZip(submissionResource.getContent(),
                ProformaSubmissionZipPathes.SUBMISSION_XML_FILE_NAME, StandardCharsets.UTF_8);
            submXmlFileBytes = submXmlContent.getBytes(StandardCharsets.UTF_8);
        }
        return XmlUtils.unmarshalToObject(submXmlFileBytes, AbstractSubmissionType.class);
    }
}
