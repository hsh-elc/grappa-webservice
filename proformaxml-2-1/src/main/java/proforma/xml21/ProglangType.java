
package proforma.xml21;

import javax.xml.bind.annotation.*;


/**
 * A list of existing programming languages is
 * available in the white paper appendix.
 *
 *
 * <p>Java class for proglang-type complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="proglang-type">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *       &lt;attribute name="version" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "proglang-type", namespace = "urn:proforma:v2.1", propOrder = {
    "value"
})
public class ProglangType {

    @XmlValue
    protected String value;
    @XmlAttribute(name = "version", required = true)
    protected String version;

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
     * Gets the value of the version property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sets the value of the version property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setVersion(String value) {
        this.version = value;
    }

}
