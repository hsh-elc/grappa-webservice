package de.hsh.grappa.utils;

import javax.xml.bind.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

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
        JAXBElement<T> j = (JAXBElement<T>) u.unmarshal(is);
        return j.getValue();
    }

    public static <T> T unmarshalToObject(byte[] byteArray, Class<T> clazz) throws Exception {
        try (ByteArrayInputStream baos = new ByteArrayInputStream(byteArray)) {
            return unmarshalToObject(baos, clazz);
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
	public String marshalToXmlUsingCDATA(Object source,Class<?>...type) throws JAXBException, IOException{
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