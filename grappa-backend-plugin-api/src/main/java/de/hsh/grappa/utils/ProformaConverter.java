package de.hsh.grappa.utils;

import de.hsh.grappa.proforma.MimeType;
import de.hsh.grappa.proforma.SubmissionResource;
import proforma.ProformaSubmissionZipPathes;
import proforma.xml.SubmissionType;

import java.nio.charset.StandardCharsets;

public class ProformaConverter {
    private ProformaConverter() {}

    public static SubmissionType convertToPojo(SubmissionResource submissionBlob) throws Exception {
        byte[] submXmlFileBytes = submissionBlob.getContent();
        if (submissionBlob.getMimeType().equals(MimeType.ZIP)) {
            String submXmlFileContent = Zip.getTextFileContentFromZip(submissionBlob.getContent(),
                ProformaSubmissionZipPathes.SUBMISSION_XML_FILE_NAME, StandardCharsets.UTF_8);
            submXmlFileBytes = submXmlFileContent.getBytes(StandardCharsets.UTF_8);
        }
        return XmlUtils.unmarshalToObject(submXmlFileBytes, SubmissionType.class);
    }
}
