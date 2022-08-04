
package proforma.xml21;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for attached-txt-file-type complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="attached-txt-file-type">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *       &lt;attribute name="encoding" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="natural-lang" type="{http://www.w3.org/2001/XMLSchema}language" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "attached-txt-file-type", namespace = "urn:proforma:v2.1", propOrder = {
    "value"
})
public class AttachedTxtFileType {

    @XmlValue
    protected String value;
    @XmlAttribute(name = "encoding")
    protected String encoding;
    @XmlAttribute(name = "natural-lang")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "language")
    protected String naturalLang;

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
     * Gets the value of the encoding property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getEncoding() {
        return encoding;
    }

    /**
     * Sets the value of the encoding property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setEncoding(String value) {
        this.encoding = value;
    }

    /**
     * Gets the value of the naturalLang property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getNaturalLang() {
        return naturalLang;
    }

    /**
     * Sets the value of the naturalLang property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setNaturalLang(String value) {
        this.naturalLang = value;
    }

}
