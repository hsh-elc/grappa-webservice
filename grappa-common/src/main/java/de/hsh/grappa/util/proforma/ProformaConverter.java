package de.hsh.grappa.util.proforma;

import de.hsh.grappa.common.Boundary;
import de.hsh.grappa.common.MimeType;
import de.hsh.grappa.common.ResponseResource;
import de.hsh.grappa.common.SubmissionResource;
import de.hsh.grappa.util.XmlUtils;
import de.hsh.grappa.util.Zip;
import proforma.ProformaResponseZipPathes;
import proforma.ProformaSubmissionZipPathes;
import proforma.xml.AbstractResponseType;
import proforma.xml.AbstractSubmissionType;

import java.nio.charset.StandardCharsets;

/**
 * Converts a Proforma resource from a ZIP or a
 * bare-bone XML to POJOs using XML binding.
 */
public class ProformaConverter {
    private ProformaConverter() {}

    public static AbstractSubmissionType convertToPojo(SubmissionResource submissionResource) throws Exception {
        byte[] submXmlFileBytes = submissionResource.getContent();
        if (submissionResource.getMimeType().equals(MimeType.ZIP)) {
            String submXmlFileContent = Zip.getTextFileContentFromZip(submissionResource.getContent(),
                ProformaSubmissionZipPathes.SUBMISSION_XML_FILE_NAME, StandardCharsets.UTF_8);
            submXmlFileBytes = submXmlFileContent.getBytes(StandardCharsets.UTF_8);
        }
        return XmlUtils.unmarshalToObject(submXmlFileBytes, AbstractSubmissionType.class);
    }

    public static AbstractResponseType convertToPojo(ResponseResource responseResource) throws Exception {
        byte[] xmlFileBytes = responseResource.getContent();
        if (responseResource.getMimeType().equals(MimeType.ZIP)) {
            String xmlFileContent = de.hsh.grappa.util.Zip.getTextFileContentFromZip(responseResource.getContent(),
                ProformaResponseZipPathes.RESPONSE_XML_FILE_NAME, StandardCharsets.UTF_8);
            xmlFileBytes = xmlFileContent.getBytes(StandardCharsets.UTF_8);
        }
        return XmlUtils.unmarshalToObject(xmlFileBytes, AbstractResponseType.class);
    }
}