package de.hsh.grappa.util;

import javax.xml.bind.*;
import javax.xml.bind.annotation.XmlSchema;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.ThreadFactory;

public class XmlUtils {
    public static String marshalToXml(Object source, Class<?>... type) {
        String result;
        StringWriter sw = new StringWriter();
        try {
            JAXBContext context = JAXBContext.newInstance(type);
            Marshaller marshaller = context.createMarshaller();
            marshaller.marshal(source, sw);
            result = sw.toString();
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
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
    
    /**
     * Marshaling POJO into XML-String using CDATA-tags if element contains XML-control-characters (instead of transform them to XML-entities) 
     * @param source: POJO that should be marshaled
     * @param type: JAXB class of given POJO
     * @return XML-String of POJO with CDATA where required.
     * @throws JAXBException
     * @throws IOException
     */
    public static String marshalToXmlUsingCDATA(Object source,Class<?>...type) throws JAXBException, IOException{
        String xmlStringWithCDATA=null;
        StringWriter sw=new StringWriter();
        
        //see: https://stackoverflow.com/questions/3136375/how-to-generate-cdata-block-using-jaxb#answer-39557646
        JAXBContext jaxbContext;
        jaxbContext=JAXBContext.newInstance(type);
        Marshaller marshaller=jaxbContext.createMarshaller();
        
        CDataContentHandler cdataHandler=new CDataContentHandler(sw,"utf-8");
        marshaller.marshal(source,cdataHandler);
        
        xmlStringWithCDATA=sw.toString();
        return xmlStringWithCDATA;
    }
}
