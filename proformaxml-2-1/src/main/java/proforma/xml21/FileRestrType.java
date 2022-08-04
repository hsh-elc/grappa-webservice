
package proforma.xml21;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for file-restr-type complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="file-restr-type">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *       &lt;attribute name="use" default="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="required"/>
 *             &lt;enumeration value="optional"/>
 *             &lt;enumeration value="prohibited"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="pattern-format" default="none">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="none"/>
 *             &lt;enumeration value="posix-ere"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "file-restr-type", namespace = "urn:proforma:v2.1", propOrder = {
    "value"
})
public class FileRestrType {

    @XmlValue
    protected String value;
    @XmlAttribute(name = "use")
    protected String use;
    @XmlAttribute(name = "pattern-format")
    protected String patternFormat;

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
     * Gets the value of the use property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getUse() {
        if (use == null) {
            return "required";
        } else {
            return use;
        }
    }

    /**
     * Sets the value of the use property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setUse(String value) {
        this.use = value;
    }

    /**
     * Gets the value of the patternFormat property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getPatternFormat() {
        if (patternFormat == null) {
            return "none";
        } else {
            return patternFormat;
        }
    }

    /**
     * Sets the value of the patternFormat property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setPatternFormat(String value) {
        this.patternFormat = value;
    }

}
