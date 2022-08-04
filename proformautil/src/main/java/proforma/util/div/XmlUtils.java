package proforma.util.div;

import javax.xml.bind.*;
import javax.xml.bind.annotation.XmlSchema;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.ThreadFactory;

public class XmlUtils {

    public static enum MarshalOption {
        /**
         * leads to marshaling POJO into XML-String using CDATA-tags if element
         * contains XML-control-characters (instead of transform them to XML-entities)
         */
        CDATA;

        private static final MarshalOption[] NONE = new MarshalOption[0];

        public static MarshalOption[] none() {
            return NONE;
        }

        public static MarshalOption[] of(MarshalOption... o) {
            return o;
        }

        public static boolean isCData(MarshalOption[] options) {
            return isContained(CDATA, options);
        }

        private static boolean isContained(MarshalOption option, MarshalOption[] options) {
            for (MarshalOption o : options) {
                if (option.equals(o)) return true;
            }
            return false;
        }
    }

    /**
     * @param source:        POJO that should be marshaled
     * @param marshalOptions options
     * @param type:          JAXB class of given POJO
     * @return XML-String of POJO
     * @throws IOException
     * @throws JAXBException
     */
    public static String marshalToXml(Object source, MarshalOption[] marshalOptions, Class<?>... type) throws IOException, JAXBException {
        String result;
        JAXBContext jaxbContext;
        jaxbContext = JAXBContext.newInstance(type);
        Marshaller marshaller = jaxbContext.createMarshaller();

        XMLOutputFactory xof = XMLOutputFactory.newInstance();
        XMLStreamWriter streamWriter = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            streamWriter = xof.createXMLStreamWriter(baos, StandardCharsets.UTF_8.name());
        } catch (XMLStreamException e) {
            throw new IOException(e);
        }

        if (MarshalOption.isCData(marshalOptions)) {
            CDataContentHandler cdataStreamWriter = new CDataContentHandler(streamWriter);
            streamWriter = cdataStreamWriter;
        }
        marshaller.setProperty(Marshaller.JAXB_ENCODING, StandardCharsets.UTF_8.name());
        marshaller.marshal(source, streamWriter);

        try {
            streamWriter.flush();
        } catch (XMLStreamException e) {
            throw new IOException(e);
        }

        result = baos.toString(StandardCharsets.UTF_8.name());
        return result;
    }

    public static <T> T unmarshalToObject(InputStream is, Class<T> clazz) throws Exception {
        JAXBContext c = JAXBContext.newInstance(clazz);
        Unmarshaller u = c.createUnmarshaller();
        @SuppressWarnings("unchecked")
        JAXBElement<T> j = (JAXBElement<T>) u.unmarshal(is);
        return j.getValue();
    }

    public static <T> T unmarshalToObject(byte[] byteArray, Class<T> clazz) throws Exception {
        try (ByteArrayInputStream baos = new ByteArrayInputStream(byteArray)) {
            return unmarshalToObject(baos, clazz);
        }
    }

    public static String getUriNamespace(Class<?> jaxbAnnotatedClass) {
        return jaxbAnnotatedClass.getPackage().getAnnotation(XmlSchema.class).namespace();
    }


    // Context, reason, and source of this class:
    // https://stackoverflow.com/questions/51518781/jaxb-not-available-on-tomcat-9-and-java-9-10
    // modified to ThreadFactory
    public static class JaxbThreadFactory implements ThreadFactory {
        private final ClassLoader classLoader;

        public JaxbThreadFactory() {
            classLoader = Thread.currentThread().getContextClassLoader();
        }

        @Override
        public Thread newThread(Runnable runnable) {
            Thread thread = new Thread(runnable);
            thread.setContextClassLoader(classLoader);
            return thread;
        }
    }

    // Context, reason, and source of this class:
    // https://stackoverflow.com/questions/51518781/jaxb-not-available-on-tomcat-9-and-java-9-10
    public class JaxbForkJoinWorkerThreadFactory implements ForkJoinPool.ForkJoinWorkerThreadFactory {
        private final ClassLoader classLoader;

        public JaxbForkJoinWorkerThreadFactory() {
            classLoader = Thread.currentThread().getContextClassLoader();
        }

        @Override
        public final ForkJoinWorkerThread newThread(ForkJoinPool pool) {
            ForkJoinWorkerThread thread = new JaxbForkJoinWorkerThread(pool);
            thread.setContextClassLoader(classLoader);
            return thread;
        }

        private class JaxbForkJoinWorkerThread extends ForkJoinWorkerThread {
            private JaxbForkJoinWorkerThread(ForkJoinPool pool) {
                super(pool);
            }
        }
    }

    public static boolean isXml(byte[] bytes) {
        int i = 0;
        while (i < bytes.length && Character.isWhitespace((char) bytes[i])) {
            i++;
        }

        return bytes.length > i + 5
            && bytes[i++] == (byte) '<'
            && bytes[i++] == (byte) '?'
            && bytes[i++] == (byte) 'x'
            && bytes[i++] == (byte) 'm'
            && bytes[i++] == (byte) 'l';
    }
}
