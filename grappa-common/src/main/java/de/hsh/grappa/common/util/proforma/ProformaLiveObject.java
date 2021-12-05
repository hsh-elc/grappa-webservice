package de.hsh.grappa.common.util.proforma;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Constructor;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hsh.grappa.common.MimeType;
import de.hsh.grappa.common.ProformaResource;
import de.hsh.grappa.util.XmlUtils;
import de.hsh.grappa.util.Zip;
import de.hsh.grappa.util.Zip.ZipContentElement;
import proforma.xml.AbstractProformaType;

/**
 * Helper class to represent a ProFormA task or submission or response in memory. This could be either a representation of
 * a XML or a ZIP file. 
 */
public abstract class ProformaLiveObject<R extends ProformaResource, P extends AbstractProformaType> {

    private static final Logger log = LoggerFactory.getLogger(ProformaLiveObject.class);
    
    protected abstract String displayName();
    protected abstract Class<R> getResourceType();
    protected abstract String getMainXmlFileName();
    
    private MimeType mimeType;

    private Map<String,ZipContentElement> zipContent;
    private byte[] xmlContent;
    private P pojo;
    private R resource;
    
    /**
     * Creates an in-memory representation of a given resources
     * @param resource The given resource
     * @throws Exception
     */
    protected ProformaLiveObject(R resource) throws Exception {
        this.mimeType= resource.getMimeType();
        this.resource = resource;
        if (resource.getMimeType().equals(MimeType.ZIP)) {
            try (ByteArrayInputStream baos = new ByteArrayInputStream(resource.getContent())) {
                zipContent= Zip.readZipFileToMap(baos);
            }
        } else {
            xmlContent= resource.getContent();
        }
    }
    
    
    protected ProformaLiveObject(P pojo, Map<String, ZipContentElement> otherZipContentExceptMainXmlFile, MimeType mimeType, Class<?> ... contextClasses) throws Exception {
        this.pojo = pojo;
        byte[] xmlBytes = toXmlBytes(pojo, pojo.getClass(), contextClasses);
        this.mimeType = mimeType;
        
        if (otherZipContentExceptMainXmlFile != null && !otherZipContentExceptMainXmlFile.isEmpty()
                || mimeType == MimeType.ZIP) {
            this.zipContent = new TreeMap<>();
            if (otherZipContentExceptMainXmlFile != null) {
                for (String k : otherZipContentExceptMainXmlFile.keySet()) {
                    this.zipContent.put(k, otherZipContentExceptMainXmlFile.get(k));
                }
                ZipContentElement elem= otherZipContentExceptMainXmlFile.get(getMainXmlFileName());
                if (elem != null) {
                    throw new IllegalArgumentException("Map of ZipContentElements should not contain file '"+getMainXmlFileName()+"'");
                }
            }

            ZipContentElement elem = new ZipContentElement(getMainXmlFileName(), xmlBytes, System.currentTimeMillis());
            this.zipContent.put(getMainXmlFileName(), elem);
        } else {
            xmlContent= xmlBytes;
        }
        
        this.resource= createResource(xmlContent, zipContent, getResourceType());
    }
    
    
    public MimeType getMimeType() {
        return mimeType;
    }
    
    public ProformaResource getResource() {
        return resource;
    }
    
    /**
     * 
     * @return a pojo deserialized from the main xml file. This pojo can be modified and stored later
     * on by calling {@link #toResource()}.
     * @throws Exception
     */
    protected <T extends P> T getPojo(Class<T> pojoType) throws Exception {
        if (pojo == null) {
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
	        pojo= XmlUtils.unmarshalToObject(xmlFileBytes, pojoType);
        }
        try {
        	return pojoType.cast(pojo);
        } catch (ClassCastException ex) {
            throw new UnsupportedOperationException("TODO: implement type '" + pojoType + "' (unknown ProFormA version '" + pojo.proFormAVersion() + "')");
        }
    }
    
    
    /**
     * This method can be called for a ZIP resource only.
     * @param path path inside the zip resource
     * @return the respective in-memory element
     * @RuntimeException if this is not a ZIP resource
     */
    public ZipContentElement getZipContentElement(String path) {
        log.debug("getZipContentElement('{}')", path);
        if (getMimeType().equals(MimeType.ZIP)) {
            log.debug("  keyset: " + zipContent.keySet());
            return zipContent.get(path);
        }
        throw new RuntimeException("This is not a ZIP resource");
    }
    

    /**
     * This method can be called for a ZIP resource only.
     * @return the live map of zip content. Anay changes will be reflected inside the object.
     * @RuntimeException if this is not a ZIP resource
     */
    public Map<String, ZipContentElement> getZipContent() {
        log.debug("getZipContent()");
        if (getMimeType().equals(MimeType.ZIP)) {
            return zipContent;
        }
        throw new RuntimeException("This is not a ZIP resource");
    }
    
    
    /**
     * Create a new resource object from the live data.
     * @param contextClasses classes needed when marshalling XML
     * @return a new resource object
     * @throws Exception
     */
    public R toResource(Class<? extends P> pojoType, Class<?> ... contextClasses) throws Exception {
        // Check, whether there were data changes via the pojo handle:
        if (pojo != null) {
            byte[] xmlBytes = toXmlBytes(pojo, pojoType, contextClasses);
            
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

        this.resource = createResource(xmlContent, zipContent, getResourceType());
        return this.resource;
    }

    private static <C extends AbstractProformaType> byte[] toXmlBytes(C pojo, Class<? extends C> clazz, Class<?> ... contextClasses) {
        Class<?>[] classes= new Class<?>[contextClasses.length + 2];
        classes[0]= clazz;
        classes[1]= pojo.getContextClass();
        System.arraycopy(contextClasses, 0, classes, 2, contextClasses.length);
        String xml= XmlUtils.marshalToXml(pojo, classes);
        byte[] xmlBytes= xml.getBytes(StandardCharsets.UTF_8);
        return xmlBytes;
    }
    
    private static <C extends ProformaResource>  C createResource(byte[] xmlContent, Map<String, ZipContentElement> zipContent, Class<C> clazz) throws Exception {
        MimeType mimeType = null;
        byte[] bytes = null;
        int cnt = 0;
        if (zipContent != null) {
            cnt++;
            mimeType= MimeType.ZIP;
            try (ByteArrayOutputStream baos= new ByteArrayOutputStream()) {
                Zip.writeMapToZipFile(zipContent, baos);
                bytes= baos.toByteArray();
            }
        } 
        if (xmlContent != null) {
            cnt++;
            mimeType= MimeType.XML;
            bytes= xmlContent;
        }
        if (cnt != 1) {
            throw new AssertionError("Unexpected missing or too many data to be converted to ProformaRessource.");
        }
        Constructor<C> c= clazz.getConstructor(byte[].class, MimeType.class); 
        return c.newInstance(bytes, mimeType);
    }



}
