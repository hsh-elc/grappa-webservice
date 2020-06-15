package de.hsh.grappa.utils;

import javax.xml.bind.*;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;

public class XmlUtils {
    public static String marshalToXml(Object source, Class... type) {
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
        JAXBElement<T> j = (JAXBElement<T>) u.unmarshal(is);
        return j.getValue();
    }

    public static <T> T unmarshalToObject(byte[] byteArray, Class<T> clazz) throws Exception {
        try (ByteArrayInputStream baos = new ByteArrayInputStream(byteArray)) {
            return unmarshalToObject(baos, clazz);
        }
    }
}