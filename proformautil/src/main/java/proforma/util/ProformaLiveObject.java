package proforma.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import proforma.util.div.XmlUtils;
import proforma.util.div.XmlUtils.MarshalOption;
import proforma.util.div.Zip;
import proforma.util.div.Zip.ZipContent;
import proforma.util.div.Zip.ZipContentElement;
import proforma.util.resource.MimeType;
import proforma.util.resource.ProformaResource;
import proforma.xml.AbstractProformaType;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Helper class to represent a ProFormA task or submission or response in memory.
 * This could be either a representation of
 * a XML or a ZIP file.
 *
 * <p>Usage scenario 1 (a task example):</p>
 * <pre>
 *   TaskResource resource= ...;
 *   ProformaVersion pv = ...;
 *   TaskLive live= new TaskLive(resource, pv);
 *   AbstractTaskType pojo= live.getTask();
 *   TaskResource res = live.getResource(); // get the original resource
 *   // make some changes in the data of pojo.
 *   pojo.set... ;
 *   live.markPojoChanged(MarshalOption.none());
 *   // create a new resource from the changes
 *   TaskResource newResource= live.getResource();
 * </pre>
 *
 * <p>Usage scenario 2 (a response example):</p>
 * <pre>
 *   AbstractResponseType pojo = ...; // this carries the ProFormA version
 *   ZipContent zip = ...;
 *   ResponseLive live = new ResponseLive(pojo, zip, MimeType.ZIP, MarshalOption.of(MarshalOption.CDATA));
 *   ResponseResource resource = live.getResource();
 * </pre>
 */
public abstract class ProformaLiveObject<R extends ProformaResource, P extends AbstractProformaType> {

    private static final Logger log = LoggerFactory.getLogger(ProformaLiveObject.class);

    protected abstract String displayName();

    protected abstract Class<R> getResourceType();

    public abstract String getMainXmlFileName();

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
     *
     * @param resource The given resource
     * @throws Exception
     */
    protected ProformaLiveObject(R resource, Class<?>... contextClasses) throws Exception {
        this.contextClasses = contextClasses;
        this.mimeType = resource.getMimeType();
        this.resource = resource;
        createContentFromResource();
    }


    protected ProformaLiveObject(P pojo, ZipContent otherZipContentExceptMainXmlFile, MimeType mimeType, MarshalOption[] marshalOptions, Class<?>... contextClasses) throws Exception {
        this.proformaVersion = ProformaVersion.getInstanceByVersionNumber(pojo.proFormAVersionNumber());
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
                ZipContentElement elem = otherZipContentExceptMainXmlFile.get(getMainXmlFileName());
                if (elem != null) {
                    throw new IllegalArgumentException("Map of ZipContentElements should not contain file '" + getMainXmlFileName() + "'");
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
            ZipContentElement elem = getMainXmlFromZipContent();
            elem.setBytes(xmlBytes);
            xmlContent = null;
        } else {
            xmlContent = xmlBytes;
        }
        this.resource = null;
    }

    public void markPojoChanged(MarshalOption[] marshalOptions) throws Exception {
        createContentFromPojo(marshalOptions);
    }


    public ProformaVersion getProformaVersion() throws Exception {
        if (proformaVersion == null) {
            detectProformaVersionFromContent();
        }
        return proformaVersion;
    }


    private void detectProformaVersionFromContent() throws ParserConfigurationException, SAXException, IOException {
        byte[] xmlFileBytes = getXmlFileBytes();
        ByteArrayInputStream bais = new ByteArrayInputStream(xmlFileBytes);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(bais);
        Element root = doc.getDocumentElement();

        ArrayList<Node> nodes = new ArrayList<>();
        nodes.add(root);

        NodeList childNodes = root.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            nodes.add(childNodes.item(i));
        }

        HashSet<String> namespaces = new HashSet<>();
        for (Node node : nodes) {
            //log.debug("detectProformaVersionFromContent: node = " + node.getNodeName() + ", " + node.getNodeType() + ", " + node.getNamespaceURI());            
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                String ns = node.getNamespaceURI();
                if (ns != null) {
                    namespaces.add(ns);
                }
            }
        }

        for (String ns : namespaces) {
            try {
                proformaVersion = ProformaVersion.getInstanceByNamespaceUri(ns);
                return;
            } catch (Throwable t) {
                // continue
            }
        }

        throw new UnsupportedOperationException("Unsupported XML file with namespace(s) " + namespaces);
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
     *
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
     *
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
        byte[] xmlFileBytes = getXmlFileBytes();
        this.pojo = XmlUtils.unmarshalToObject(xmlFileBytes, pojoType);
    }

    private byte[] getXmlFileBytes() {
        byte[] xmlFileBytes;
        if (xmlContent != null) {
            xmlFileBytes = xmlContent;
        } else if (zipContent != null) {
            ZipContentElement elem = getMainXmlFromZipContent();
            xmlFileBytes = elem.getBytes();
        } else {
            throw new AssertionError("Neither xml nor zip content in " + ProformaLiveObject.class + "::createPojoFromContent");
        }
        return xmlFileBytes;
    }


//    /**
//     * Create a new resource object from the live data.
//     * @param contextClasses classes needed when marshalling XML
//     * @return a new resource object
//     * @throws Exception
//     */
//    public R toResource(MarshalOption[] marshalOptions, Class<?> ... contextClasses) throws Exception {
//        // Check, whether there were data changes via the pojo handle:
//        if (pojo != null) {
//            byte[] xmlBytes = toXmlBytes(pojo, marshalOptions, contextClasses);
//            
//            if (zipContent != null) {
//                ZipContentElement elem= getMainXmlFromZipContent();
//                elem.setBytes(xmlBytes);
//            } else {
//                xmlContent= xmlBytes;
//            }
//            
//        } // else: nothing to do, since the Pojo has never been created
//    
//        createResourceFromContent();
//        return this.resource;
//    }


    private ZipContentElement getMainXmlFromZipContent() {
        ZipContentElement elem = zipContent.get(getMainXmlFileName());
        if (elem == null) {
            throw new IllegalArgumentException(displayName() + " lacks file '" + getMainXmlFileName() + "'");
        }
        return elem;
    }


    private static <C extends AbstractProformaType> byte[] toXmlBytes(C pojo, MarshalOption[] marshalOptions, Class<?>... contextClasses) throws IOException, JAXBException {
        Class<?>[] classes = new Class<?>[contextClasses.length + 1];
        classes[0] = pojo.getClass();
        System.arraycopy(contextClasses, 0, classes, 1, contextClasses.length);
        String xml = XmlUtils.marshalToXml(pojo, marshalOptions, classes);
        byte[] xmlBytes = xml.getBytes(StandardCharsets.UTF_8);
        return xmlBytes;
    }

    private void createResourceFromContent() throws Exception {
        nowDoMarshalPojoToContent();
        MimeType mimeType = null;
        byte[] bytes = null;
        int cnt = 0;
        if (zipContent != null) {
            cnt++;
            mimeType = MimeType.ZIP;
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                Zip.writeMapToZipFile(zipContent, baos);
                bytes = baos.toByteArray();
            }
        }
        if (xmlContent != null) {
            cnt++;
            mimeType = MimeType.XML;
            bytes = xmlContent;
        }
        if (cnt != 1) {
            throw new AssertionError("Unexpected missing or too many data to be converted to ProformaRessource.");
        }
        Constructor<R> c = getResourceType().getConstructor(byte[].class, MimeType.class);
        this.resource = c.newInstance(bytes, mimeType);
    }


    private void createContentFromResource() throws Exception {
        if (resource.getMimeType().equals(MimeType.ZIP)) {
            try (ByteArrayInputStream baos = new ByteArrayInputStream(resource.getContent())) {
                zipContent = Zip.readZipFileToMap(baos);
            }
        } else {
            xmlContent = resource.getContent();
        }
    }


}
