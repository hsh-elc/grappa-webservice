package proforma.util;

import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;

/**
 * This class is obsolete and not used anywhere in this
 * project anymore.
 * It is still kept for future reference and/or use.
 */
@Deprecated
class ProFormAXmlUriInspector {
    public String getUriFromXml(InputStream is) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(is);
        Element root = doc.getDocumentElement();
        //prints root name space
        printAttributesInfo((Node) root);

        NodeList childNodes = root.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            printAttributesInfo(childNodes.item(i));
        }
        return null;
    }

    public static void printAttributesInfo(Node root) {
        NamedNodeMap attributes = root.getAttributes();
        if (attributes != null) {
            for (int i = 0; i < attributes.getLength(); i++) {
                Node node = attributes.item(i);
                if (node.getNodeType() == Node.ATTRIBUTE_NODE) {
                    String name = node.getNodeName();
                    System.out.println(name + " " + node.getNamespaceURI());
                }
            }
        }
    }
}
