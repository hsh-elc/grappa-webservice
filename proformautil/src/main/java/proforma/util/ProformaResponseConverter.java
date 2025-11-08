package proforma.util;

import proforma.util.div.XmlUtils;
import proforma.util.div.Zip;
import proforma.util.resource.MimeType;
import proforma.util.resource.ResponseResource;

import java.io.IOException;

/**
 * Utility task for response conversion
 */
public class ProformaResponseConverter {

    /**
     * Convert the format of a Response
     *
     * @param response The response that should be converted
     * @param requestedResponseFormat The Format to convert to as string ("xml" or "zip")
     * @return A new converted Response
     * @throws Exception
     */
    public static ResponseResource convertResponseFormat(ResponseResource response, String requestedResponseFormat) throws Exception {
        MimeType respType = response.getMimeType();
        if (respType.equals(MimeType.XML) && requestedResponseFormat.equals("zip")) {
            response = convertXMLToZIP(response);
        } else if (respType.equals(MimeType.ZIP) && requestedResponseFormat.equals("xml")) {
            response = convertZIPToXML(response);
        }
        return response;
    }

    /**
     * Convert the format of the response from xml to zip.
     * The result is a zip that only contains the original response.xml
     *
     * @param response The response that should be converted
     * @return The converted response
     * @throws IOException
     */
    private static ResponseResource convertXMLToZIP(ResponseResource response) throws IOException {
        byte[] zippedResponseContent = Zip.wrapSingleFileIntoZip(response.getContent(), ProformaResponseZipPathes.RESPONSE_XML_FILE_NAME);
        return new ResponseResource(zippedResponseContent, MimeType.ZIP);
    }

    /**
     * Convert the format of the response from zip to xml.
     * All attached files will be converted to embedded files.
     *
     * @param response The response that should be converted
     * @return The converted response
     * @throws Exception
     */
    private static ResponseResource convertZIPToXML(ResponseResource response) throws Exception {
        ResponseLive responseLive = new ResponseLive(response);

        //Convert all attached files to embedded files
        for (ProformaResponseFileHandle prfh : responseLive.getResponseFileHandles()) {
            prfh.convertResponseFileToEmbedded(false, false);
        }
        responseLive.markPojoChanged(XmlUtils.MarshalOption.of(XmlUtils.MarshalOption.CDATA));

        //Convert response.zip -> response.xml (just retrieve the now converted response.xml from responseLive)
        byte[] unzippedResponseContent = responseLive.getZipContent().get(ProformaResponseZipPathes.RESPONSE_XML_FILE_NAME).getBytes();
        return new ResponseResource(unzippedResponseContent, MimeType.XML);
    }
}
