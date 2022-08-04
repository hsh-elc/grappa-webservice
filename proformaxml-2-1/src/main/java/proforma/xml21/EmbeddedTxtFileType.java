
package proforma.xml21;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for embedded-txt-file-type complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="embedded-txt-file-type">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *       &lt;attribute name="filename" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "embedded-txt-file-type", namespace = "urn:proforma:v2.1", propOrder = {
    "value"
})
public class EmbeddedTxtFileType {

    @XmlValue
    protected String value;
    @XmlAttribute(name = "filename", required = true)
    protected String filename;

    /**
     * Gets the value of the value property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Gets the value of the filename property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Sets the value of the filename property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setFilename(String value) {
        this.filename = value;
    }

}
