package proforma.util.div;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.regex.Pattern;

class CDataContentHandler extends DelegatingXMLStreamWriter {

    private static final Pattern XML_CHARS = Pattern.compile("[&<>]");

    public CDataContentHandler(XMLStreamWriter writer) {
        super(writer);
    }

    /**
     * Write text to the output
     *
     * @param text  the value to write
     * @param start the starting position in the array
     * @param len   the number of characters to write
     * @throws XMLStreamException
     */
    public void writeCharacters(char[] text, int start, int len) throws XMLStreamException {
        writeCharacters(new String(text, start, len));
    }

    @Override
    public void writeCharacters(String text) throws XMLStreamException {
        boolean useCData = XML_CHARS.matcher(text).find() && !text.contains("]]>");
        if (useCData) {
            super.writeCData(text);
        } else {
            super.writeCharacters(text);
        }
    }
}
