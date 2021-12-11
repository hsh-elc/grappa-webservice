package de.hsh.grappa.common.util.proforma;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.charset.StandardCharsets;

import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hsh.grappa.common.MimeType;
import de.hsh.grappa.common.ProformaResource;
import de.hsh.grappa.util.XmlUtils;
import de.hsh.grappa.util.XmlUtils.MarshalOption;
import de.hsh.grappa.util.Zip;
import de.hsh.grappa.util.Zip.ZipContent;
import de.hsh.grappa.util.Zip.ZipContentElement;
import proforma.xml.AbstractProformaType;

/**
 * Helper class to represent a ProFormA task or submission or response in memory. 
 * This could be either a representation of
 * a XML or a ZIP file. 
 */
public abstract class ProformaLiveObject<R extends ProformaResource, P extends AbstractProformaType> {

    private static final Logger log = LoggerFactory.getLogger(ProformaLiveObject.class);
    
    protected abstract String displayName();
    protected abstract Class<R> getResourceType();
    protected abstract String getMainXmlFileName();
    
    private MimeType mimeType;

    private ZipContent zipContent;
    private byte[] xmlContent;
    private P pojo;
    private R resource;
    private ProformaVersion proformaVersion;
    private Class<?>[] contextClasses;
    
    @FunctionalInterface
    public interface CheckedRunnable {
        void run() throws Exception;
    }
    
    private CheckedRunnable lazyMarshalPojoToContent;
    
    
    /**
     * Creates an in-memory representation of a given resources
     * @param resource The given resource
     * @throws Exception
     */
    protected ProformaLiveObject(R resource, ProformaVersion version, Class<?> ... contextClasses) throws Exception {
        this.proformaVersion = version;
        this.contextClasses = contextClasses;
        this.mimeType= resource.getMimeType();
        this.resource = resource;
        createContentFromResource();
    }

    
    protected ProformaLiveObject(P pojo, ZipContent otherZipContentExceptMainXmlFile, MimeType mimeType, MarshalOption[] marshalOptions, Class<?> ... contextClasses) throws Exception {
    	this.proformaVersion = ProformaVersion.getInstance(pojo.proFormAVersionNumber());
        this.contextClasses = contextClasses;
        this.pojo = pojo;
        this.mimeType = mimeType;
        
        if (otherZipContentExceptMainXmlFile != null && !otherZipContentExceptMainXmlFile.isEmpty()
                || mimeType == MimeType.ZIP) {
            this.zipContent = new ZipContent();
            if (otherZipContentExceptMainXmlFile != null) {
                for (String k : otherZipContentExceptMainXmlFile.keySet()) {
                    this.zipContent.put(k, otherZipContentExceptMainXmlFile.get(k));
                }
                ZipContentElement elem= otherZipContentExceptMainXmlFile.get(getMainXmlFileName());
                if (elem != null) {
                    throw new IllegalArgumentException("Map of ZipContentElements should not contain file '"+getMainXmlFileName()+"'");
                }
            }
            
            byte[] dummyMainXmlContent = {}; // the real content will be created by below call to createContentFromPojo
            ZipContentElement elem = new ZipContentElement(getMainXmlFileName(), dummyMainXmlContent, System.currentTimeMillis());
            this.zipContent.put(getMainXmlFileName(), elem);
        }

        lazyMarshalPojoToContent = () -> createContentFromPojo(marshalOptions);
    }

    private void createContentFromPojo(MarshalOption[] marshalOptions) throws IOException, JAXBException {
        byte[] xmlBytes = toXmlBytes(pojo, marshalOptions, contextClasses);
        
        if (zipContent != null) {
            ZipContentElement elem= getMainXmlFromZipContent();
            elem.setBytes(xmlBytes);
            xmlContent = null;
        } else {
            xmlContent= xmlBytes;
        }
	    this.resource = null;
    }    

    public void markPojoChanged(MarshalOption[] marshalOptions) throws Exception {
	    createContentFromPojo(marshalOptions);
    }
    
    
    public ProformaVersion getProformaVersion() {
    	return proformaVersion;
    }
    
    
    public MimeType getMimeType() {
        return mimeType;
    }
    
    public Class<?>[] getContextClasses() {
    	return contextClasses;
    }
    
    public ProformaResource getResource() throws Exception {
    	if (resource == null) {
    		createResourceFromContent();
    	}
        return resource;
    }
    
    private void nowDoMarshalPojoToContent() throws Exception {
    	if (lazyMarshalPojoToContent != null) {
    		lazyMarshalPojoToContent.run();
    		lazyMarshalPojoToContent = null;
    	}
    }
    
    /**
     * 
     * @return a pojo deserialized from the main xml file. This pojo can be modified and stored later
     * on by calling {@link #markPojoChanged(MarshalOption[], Class...)}.
     * @throws Exception
     */
    protected <T extends P> T getPojo(Class<T> pojoType) throws Exception {
        if (pojo == null) {
        	createPojoFromContent(pojoType);
        }
        try {
        	return pojoType.cast(pojo);
        } catch (ClassCastException ex) {
            throw new UnsupportedOperationException("TODO: implement type '" + pojoType + "' (unknown ProFormA version '" + pojo.proFormAVersionNumber() + "')");
        }
    }
    

    /**
     * This method can be called for a ZIP resource only.
     * @param path path inside the zip resource
     * @return the respective in-memory element
     * @throws Exception 
     * @RuntimeException if this is not a ZIP resource
     */
    public ZipContentElement getZipContentElement(String path) throws Exception {
        log.trace("getZipContentElement('{}')", path);
    	nowDoMarshalPojoToContent();
        if (getMimeType().equals(MimeType.ZIP)) {
            log.trace("  keyset: " + zipContent.keySet());
            return zipContent.get(path);
        }
        throw new RuntimeException("This is not a ZIP resource");
    }
    

    /**
     * This method can be called for a ZIP resource only.
     * @return the live map of zip content. Anay changes will be reflected inside the object.
     * @throws Exception 
     * @RuntimeException if this is not a ZIP resource
     */
    public ZipContent getZipContent() throws Exception {
        log.trace("getZipContent()");
    	nowDoMarshalPojoToContent();
        if (getMimeType().equals(MimeType.ZIP)) {
            return zipContent;
        }
        throw new RuntimeException("This is not a ZIP resource");
    }
    
    
    

    private <T extends P> void createPojoFromContent(Class<T> pojoType) throws Exception {
        byte[] xmlFileBytes;
        if (xmlContent != null) {
            xmlFileBytes= xmlContent;
        } else if (zipContent != null) {
            ZipContentElement elem= getMainXmlFromZipContent();
            xmlFileBytes= elem.getBytes();
        } else {
        	throw new AssertionError("Neither xml nor zip content in " + ProformaLiveObject.class + "::createPojoFromContent");
        }
        this.pojo= XmlUtils.unmarshalToObject(xmlFileBytes, pojoType);
    }
    

    
//    /**
//     * Create a new resource object from the live data.
//     * @param contextClasses classes needed when marshalling XML
//     * @return a new resource object
//     * @throws Exception
//     */
//    public R toResource(MarshalOption[] marshalOptions, Class<?> ... contextClasses) throws Exception {
//	    // Check, whether there were data changes via the pojo handle:
//	    if (pojo != null) {
//	        byte[] xmlBytes = toXmlBytes(pojo, marshalOptions, contextClasses);
//	        
//	        if (zipContent != null) {
//	            ZipContentElement elem= getMainXmlFromZipContent();
//	            elem.setBytes(xmlBytes);
//	        } else {
//	            xmlContent= xmlBytes;
//	        }
//	        
//	    } // else: nothing to do, since the Pojo has never been created
//	
//	    createResourceFromContent();
//	    return this.resource;
//	}
    
    
    private ZipContentElement getMainXmlFromZipContent() {
        ZipContentElement elem= zipContent.get(getMainXmlFileName());
        if (elem == null) {
            throw new IllegalArgumentException(displayName() + " lacks file '"+getMainXmlFileName()+"'");
        }
        return elem;
    }
    

    

    private static <C extends AbstractProformaType> byte[] toXmlBytes(C pojo, MarshalOption[] marshalOptions, Class<?> ... contextClasses) throws IOException, JAXBException {
        Class<?>[] classes= new Class<?>[contextClasses.length + 1];
        classes[0]= pojo.getClass();
        System.arraycopy(contextClasses, 0, classes, 1, contextClasses.length);
        String xml= XmlUtils.marshalToXml(pojo, marshalOptions, classes);
        byte[] xmlBytes= xml.getBytes(StandardCharsets.UTF_8);
        return xmlBytes;
    }
    
    private void createResourceFromContent() throws Exception {
    	nowDoMarshalPojoToContent();
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
        Constructor<R> c= getResourceType().getConstructor(byte[].class, MimeType.class); 
        this.resource = c.newInstance(bytes, mimeType);
    }


    private void createContentFromResource() throws Exception {
        if (resource.getMimeType().equals(MimeType.ZIP)) {
try (FileOutputStream fos = new FileOutputStream("D:\\sub-proformalive.zip")) {
	fos.write(resource.getContent());
}
System.out.println(resource.getMimeType());
System.out.println(java.util.Arrays.toString(resource.getContent()));
            try (ByteArrayInputStream baos = new ByteArrayInputStream(resource.getContent())) {
                zipContent= Zip.readZipFileToMap(baos);
            }
        } else {
            xmlContent= resource.getContent();
        }
    }
    

    
}
