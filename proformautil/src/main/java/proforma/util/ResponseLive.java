package proforma.util;

import proforma.util.div.XmlUtils.MarshalOption;
import proforma.util.div.Zip.ZipContent;
import proforma.util.resource.MimeType;
import proforma.util.resource.ResponseResource;
import proforma.xml.AbstractResponseType;

import java.util.List;

/**
 * <p>Helper class to represent a ProFormA response in memory. This could be either a representation of
 * a XML or a ZIP file. </p>
 *
 * <p>For usage scenarios see {@link ProformaLiveObject}.</p>
 */
public class ResponseLive extends ProformaLiveObject<ResponseResource, AbstractResponseType> {

    /**
     * Creates an in-memory representation of a given response resource
     *
     * @param resource The given resource
     * @throws Exception
     */
    public ResponseLive(ResponseResource resource, Class<?>... contextClasses) throws Exception {
        super(resource, contextClasses);
    }

    /**
     * Creates an object from an in-memory representation and augments this with additional
     * data to be written to a ZIP or XML file when serialized.
     *
     * @param mimeType must be ZIP, if {@code otherZipContentExceptMainXmlFile} is not empty.
     */
    public ResponseLive(AbstractResponseType response, ZipContent otherZipContentExceptMainXmlFile, MimeType mimeType, MarshalOption[] marshalOptions, Class<?>... contextClasses) throws Exception {
        super(response, otherZipContentExceptMainXmlFile, mimeType, marshalOptions, contextClasses);
    }

    /**
     * @return the string "response"
     */
    @Override
    public String displayName() {
        return "response";
    }

    /**
     * @return a pojo deserialized from the response.xml file. This pojo can be modified and stored later
     * on by calling {@link #markPojoChanged(MarshalOption[], Class...)}.
     * @throws Exception
     */
    public <T extends AbstractResponseType> T getResponse() throws Exception {
        return super.getPojo(getProformaVersion().getResponseHelper().getPojoType());
    }

    /**
     * @return the original resource or the new resource created by {@link #toResource(Class...)}.
     * @throws Exception
     */
    @Override
    public ResponseResource getResource() throws Exception {
        return (ResponseResource) super.getResource();
    }


    @Override
    protected Class<ResponseResource> getResourceType() {
        return ResponseResource.class;
    }


    @Override
    public String getMainXmlFileName() {
        return ProformaResponseZipPathes.RESPONSE_XML_FILE_NAME;
    }

    /**
     * @return a list of handles allowing access to embedded and attached files.
     * @throws Exception
     */
    public List<? extends ProformaResponseFileHandle> getResponseFileHandles() throws Exception {
        ZipContent zc = null;
        if (MimeType.ZIP.equals(getMimeType())) {
            zc = getZipContent();
        }
        return getProformaVersion().getResponseHelper().getResponseFileHandles(getResponse(), zc);
    }
}
