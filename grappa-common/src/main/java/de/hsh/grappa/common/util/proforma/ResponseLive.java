package de.hsh.grappa.common.util.proforma;

import java.util.Map;

import de.hsh.grappa.common.MimeType;
import de.hsh.grappa.common.ResponseResource;
import de.hsh.grappa.util.Zip.ZipContentElement;
import proforma.ProformaResponseZipPathes;
import proforma.xml.AbstractResponseType;

/**
 * <p>Helper class to represent a ProFormA response in memory. This could be either a representation of
 * a XML or a ZIP file. </p>
 * 
 * <p>Usage:</p>
 * <pre>
 * ResponseResource resource= ...;
 * ResponseLive live= new ResponseLive(resource);
 * ProformaResponseHelper ph = ...;  // e. g. ... = ProformaVersion.getResponseHelper();
 * AbstractResponseType pojo= live.getResponse(ph.getPojoType());
 * ResponseResource res = live.getResource(); // get the original resource
 * // make some changes in the data of pojo.
 * pojo.set... ;
 * // create a new resource from the changes
 * ResponseResource newResource= live.toResource(ph.getPojoType(), &lt;grader-specific-JAXB-classes&gt;);
 * live.getResource(); // returns the new resource
 * </pre>
 */
public class ResponseLive extends ProformaLiveObject<ResponseResource, AbstractResponseType>{

    /**
     * Creates an in-memory representation of a given response resource
     * @param resource The given resource
     * @throws Exception
     */
    public ResponseLive(ResponseResource resource) throws Exception {
        super(resource);
    }

    /**
     * Creates an object from an in-memory representation and augments this with additional
     * data to be written to a ZIP or XML file when serialized.
     * @param mimeType must be ZIP, if {@code otherZipContentExceptMainXmlFile} is not empty.
     */
    public ResponseLive(AbstractResponseType response, Map<String, ZipContentElement> otherZipContentExceptMainXmlFile, MimeType mimeType, Class<?> ... contextClasses) throws Exception {
        super(response, otherZipContentExceptMainXmlFile, mimeType, contextClasses);
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
     * on by calling {@link #toResource()}.
     * @throws Exception
     */
    public <T extends AbstractResponseType> T getResponse(Class<T> clazz) throws Exception {
        return super.getPojo(clazz);
    }
    
    /**
     * @return the original resource or the new resource created by {@link #toResource(Class...)}.
     */
    @Override public ResponseResource getResource() {
        return (ResponseResource)super.getResource();
    }
    

    
    /**
     * Create a new resource object from the live data.
     * @param contextClasses classes needed when marshalling XML
     * @return a new resource object
     * @throws Exception
     */
    @Override
    public ResponseResource toResource(Class<? extends AbstractResponseType> pojoType, Class<?> ... contextClasses) throws Exception {
        return (ResponseResource) super.toResource(pojoType, contextClasses);
    }

    
    @Override
    protected Class<ResponseResource> getResourceType() {
        return ResponseResource.class;
    }


    @Override
    protected String getMainXmlFileName() {
        return ProformaResponseZipPathes.RESPONSE_XML_FILE_NAME;
    }

    
}
