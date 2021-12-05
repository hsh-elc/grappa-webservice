package de.hsh.grappa.util.proforma;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hsh.grappa.common.MimeType;
import de.hsh.grappa.common.ProformaResource;
import de.hsh.grappa.util.FilenameUtils;
import de.hsh.grappa.util.Strings;
import de.hsh.grappa.util.XmlUtils;
import de.hsh.grappa.util.Zip;
import de.hsh.grappa.util.Zip.ZipContentElement;
import proforma.xml.AbstractProformaType;
import proforma.xml.AttachedTxtFileType;
import proforma.xml.EmbeddedBinFileType;
import proforma.xml.EmbeddedTxtFileType;

/**
 * Helper class to represent a ProFormA task or submission or response in memory. This could be either a representation of
 * a XML or a ZIP file. 
 */
public abstract class ProformaLiveObject<R extends ProformaResource, P extends AbstractProformaType> {

    private static final Logger log = LoggerFactory.getLogger(ProformaLiveObject.class);
    
    /**
     * Data of an attached or embedded file
     */
    public static class FileInfo {
        private String id;
        private String mimetype;
        private String filename;
        private byte[] binContent;
        private String txtContent;
        public FileInfo(String id, String mimetype, String filename, byte[] binContent) {
            this.id = id;
            this.mimetype = mimetype;
            this.filename = filename;
            this.binContent = binContent;
        }
        public FileInfo(String id, String mimetype, String filename, String txtContent) {
            this.id = id;
            this.mimetype = mimetype;
            this.filename = filename;
            this.txtContent = txtContent;
        }
        
        public String getId() {
            return id;
        }
        public String getMimetype() {
            return mimetype;
        }
        public String getFilename() {
            return filename;
        }
        public byte[] getBinContent() {
            return binContent;
        }
        public String getTxtContent() {
            return txtContent;
        }
    }
    
    
    protected abstract String displayName();
    protected abstract Class<R> getResourceType();
    protected abstract Class<P> getPojoType();
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
        byte[] xmlBytes = toXmlBytes(pojo, getPojoType(), contextClasses);
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
    protected P getPojo() throws Exception {
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
    
    protected <T extends P> T getPojoAs(Class<T> clazz) throws Exception {
        AbstractProformaType pojo = getPojo();
        try {
            return clazz.cast(pojo);
        } catch (ClassCastException ex) {
            throw new UnsupportedOperationException("TODO: implement type '" + getPojo().getClass() + "' (unknown ProFormA version '" + pojo.proFormAVersion() + "')");
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
     * Read information about a file that is represented by a ProFormA file choice group.
     * Only one of the four file parameters is allowed. The other three must be null.
     * @param id optional
     * @param mimetype optional
     * @param embeddedBinFile
     * @param embeddedTxtFile
     * @param attachedBinFile
     * @param attachedTxtFile
     * @param optionalRootFolder This folder is prepended to any path spec in any of the attached... elements.
     * @return
     * @throws UnsupportedEncodingException
     */
    public FileInfo getFromFileChoiceGroup(
            String id,
            String mimetype,
            EmbeddedBinFileType embeddedBinFile,
            EmbeddedTxtFileType embeddedTxtFile,
            String attachedBinFile,
            AttachedTxtFileType attachedTxtFile,
            String optionalRootFolder) throws UnsupportedEncodingException {
        int cnt = 0;
        FileInfo result = null;
        if (embeddedBinFile != null) {
            cnt++;
            result = new FileInfo(id, mimetype, embeddedBinFile.getFilename(), embeddedBinFile.getValue());
        }
        if (embeddedTxtFile != null) {
            cnt++;
            result = new FileInfo(id, mimetype, embeddedTxtFile.getFilename(), embeddedTxtFile.getValue());
        }
        if (attachedBinFile != null) {
            cnt++;
            String path = concat(optionalRootFolder, attachedBinFile);
            ZipContentElement elem = getZipContentElement(path);
            if (elem == null) {
                throw new IllegalArgumentException("the path '" + path + "' does not exist in the zip contents.");
            }
            result = new FileInfo(id, mimetype, path, elem.getBytes());
        }
        if (attachedTxtFile != null) {
            cnt++;
            String path = concat(optionalRootFolder, attachedTxtFile.getValue());
            ZipContentElement elem = getZipContentElement(path);
            if (elem == null) {
                throw new IllegalArgumentException("the path '" + path + "' does not exist in the zip contents.");
            }
            String encoding = attachedTxtFile.getEncoding();
            if (Strings.isNullOrEmpty(encoding)) {
                // TODO: guess encoding from language and content.
                encoding = StandardCharsets.UTF_8.name();
            }
               result = new FileInfo(id, mimetype, path, new String(elem.getBytes(), encoding));
        }
        if (cnt != 1) {
            throw new IllegalArgumentException("file choice group should have a single file");
        }
        return result;
    }
    
    private static String concat(String optionalRootFolder, String path) {
        if (Strings.isNullOrEmpty(optionalRootFolder)) return path;
        return optionalRootFolder + "/" + path;
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
            byte[] xmlBytes = toXmlBytes(pojo, getPojoType(), contextClasses);
            
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

    private static <C extends AbstractProformaType> byte[] toXmlBytes(C pojo, Class<C> clazz, Class<?> ... contextClasses) {
        Class<?>[] classes= new Class<?>[contextClasses.length + 1];
        classes[0]= clazz;
        System.arraycopy(contextClasses, 0, classes, 1, contextClasses.length);
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
