package de.hsh.grappa.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Constructor;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import de.hsh.grappa.proforma.MimeType;
import de.hsh.grappa.proforma.ProformaResource;
import de.hsh.grappa.utils.Zip.ZipContentElement;

/**
 * Helper class to represent a ProFormA task or submission or response in memory. This could be either a representation of
 * a XML or a ZIP file. 
 */
public abstract class ProformaLiveObject<R extends ProformaResource, P> {
    
    protected abstract String displayName();
    protected abstract Class<R> getResourceType();
    protected abstract Class<P> getPojoType();
    protected abstract String getMainXmlFileName();
    
    private MimeType mimeType;

    private Map<String,ZipContentElement> zipContent;
    private byte[] xmlContent;
    private Object pojo;
    
    /**
     * Creates an in-memory representation of a given resources
     * @param resource The given resource
     * @throws Exception
     */
    protected ProformaLiveObject(ProformaResource resource) throws Exception {
        this.mimeType= resource.getMimeType();
        if (resource.getMimeType().equals(MimeType.ZIP)) {
            try (ByteArrayInputStream baos = new ByteArrayInputStream(resource.getContent())) {
                zipContent= Zip.readZipFileToMap(baos);
            }
        } else {
            xmlContent= resource.getContent();
        }
    }
    
    public MimeType getMimeType() {
        return mimeType;
    }
    
    /**
     * 
     * @return a pojo deserialized from the main xml file. This pojo can be modified and stored later
     * on by calling {@link #toResource()}.
     * @throws Exception
     */
    protected Object getPojo() throws Exception {
        if (pojo != null) return pojo;
        byte[] xmlFileBytes;
        if (xmlContent != null) {
            xmlFileBytes= xmlContent;
        } else {
            ZipContentElement elem= zipContent.get(getMainXmlFileName());
            if (elem == null) {
                throw new IllegalArgumentException("Submission lacks file '"+getMainXmlFileName()+"'");
            }
            xmlFileBytes= elem.getBytes();
        }
        pojo= XmlUtils.unmarshalToObject(xmlFileBytes, getPojoType());
        return pojo;
    }
    
    /**
     * This method can be called for a ZIP resource only.
     * @param path path inside the zip resource
     * @return the respective in-memory element
     * @RuntimeException if this is not a ZIP resource
     */
    public ZipContentElement getZipContentElement(String path) {
        if (getMimeType().equals(MimeType.ZIP)) {
            return zipContent.get(path);
        }
        throw new RuntimeException("This is not a ZIP resource");
    }
    
    /**
     * Create a new resource object from the live data.
     * @param contextClasses classes needed when marshalling XML
     * @return a new resource object
     * @throws Exception
     */
    public R toResource(Class<?> ... contextClasses) throws Exception {
        // Check, whether there were data changes via the pojo handle:
        if (pojo != null) {
            Class<?>[] classes= new Class<?>[contextClasses.length + 1];
            classes[0]= getPojoType();
            System.arraycopy(contextClasses, 0, classes, 1, contextClasses.length);
            String xml= XmlUtils.marshalToXml(pojo, classes);
            byte[] xmlBytes= xml.getBytes(StandardCharsets.UTF_8);
            
            if (zipContent != null) {
                ZipContentElement elem= zipContent.get(getMainXmlFileName());
                if (elem == null) {
                    throw new IllegalArgumentException(displayName() + " lacks file '"+getMainXmlFileName()+"'");
                }
                elem.setBytes(xmlBytes);
            } else {
                xmlContent= xmlBytes;
            }
            
        } // else: nothing to do, since the Pojo has never been created

        MimeType mimeType;
        byte[] bytes;
        if (zipContent != null) {
            mimeType= MimeType.ZIP;
            try (ByteArrayOutputStream baos= new ByteArrayOutputStream()) {
                Zip.writeMapToZipFile(zipContent, baos);
                bytes= baos.toByteArray();
            }
        } else if (xmlContent != null) {
            mimeType= MimeType.XML;
            bytes= xmlContent;
        } else {
            throw new AssertionError("Unexpected missing data to be converted to SubmissionRessource.");
        }
        Constructor<R> c= getResourceType().getConstructor(byte[].class, MimeType.class); 
        return c.newInstance(bytes, MimeType.ZIP);
    }
}
