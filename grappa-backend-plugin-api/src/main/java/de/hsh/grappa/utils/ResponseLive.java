package de.hsh.grappa.utils;

import de.hsh.grappa.proforma.ResponseResource;
import proforma.ProformaResponseZipPathes;
import proforma.xml.ResponseType;

/**
 * <p>Helper class to represent a ProFormA response in memory. This could be either a representation of
 * a XML or a ZIP file. </p>
 * 
 * <p>Usage:</p>
 * <pre>
 * ResponseResource resource= ...;
 * ResponseLive live= new ResponseLive(resource);
 * ResponseType pojo= live.getSubmission();
 * // make some changes in the data of pojo.
 * pojo.set... ;
 * ResponseResource newResource= live.toResource(&lt;grader-specific-JAXB-classes&gt;);
 * </pre>
 */
public class ResponseLive extends ProformaLiveObject<ResponseResource, ResponseType>{

    /**
     * Creates an in-memory representation of a given response resource
     * @param resource The given resource
     * @throws Exception
     */
    public ResponseLive(ResponseResource resource) throws Exception {
        super(resource);
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
    public ResponseType getResponse() throws Exception {
        return (ResponseType) super.getPojo();
    }
    
    /**
     * Create a new resource object from the live data.
     * @param contextClasses classes needed when marshalling XML
     * @return a new resource object
     * @throws Exception
     */
    @Override
    public ResponseResource toResource(Class<?> ... contextClasses) throws Exception {
        return (ResponseResource) super.toResource(contextClasses);
    }

    
    @Override
    protected Class<ResponseResource> getResourceType() {
        return ResponseResource.class;
    }

    @Override
    protected Class<ResponseType> getPojoType() {
        return ResponseType.class;
    }

    @Override
    protected String getMainXmlFileName() {
        return ProformaResponseZipPathes.RESPONSE_XML_FILE_NAME;
    }

    
}
