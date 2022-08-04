
package proforma.xml21;

import javax.xml.bind.annotation.*;


/**
 * A "test-ref" node points to a test in a ProFormA task. As such the result of the
 * pointed at test is obtained and included in a bottom-up fashion in the calculation of the total result.
 *
 *
 * <p>Java class for grades-test-ref-child-type complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="grades-test-ref-child-type">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:proforma:v2.1}grades-base-ref-child-type">
 *       &lt;sequence>
 *         &lt;element name="title" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="description" type="{urn:proforma:v2.1}description-type" minOccurs="0"/>
 *         &lt;element name="internal-description" type="{urn:proforma:v2.1}description-type" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="ref" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="sub-ref" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "grades-test-ref-child-type", namespace = "urn:proforma:v2.1", propOrder = {
    "title",
    "description",
    "internalDescription"
})
public class GradesTestRefChildType
    extends GradesBaseRefChildType {

    @XmlElement(namespace = "urn:proforma:v2.1")
    protected String title;
    @XmlElement(namespace = "urn:proforma:v2.1")
    protected String description;
    @XmlElement(name = "internal-description", namespace = "urn:proforma:v2.1")
    protected String internalDescription;
    @XmlAttribute(name = "ref", required = true)
    protected String ref;
    @XmlAttribute(name = "sub-ref")
    protected String subRef;

    /**
     * Gets the value of the title property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the value of the title property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setTitle(String value) {
        this.title = value;
    }

    /**
     * Gets the value of the description property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the internalDescription property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getInternalDescription() {
        return internalDescription;
    }

    /**
     * Sets the value of the internalDescription property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setInternalDescription(String value) {
        this.internalDescription = value;
    }

    /**
     * Gets the value of the ref property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getRef() {
        return ref;
    }

    /**
     * Sets the value of the ref property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setRef(String value) {
        this.ref = value;
    }

    /**
     * Gets the value of the subRef property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getSubRef() {
        return subRef;
    }

    /**
     * Sets the value of the subRef property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setSubRef(String value) {
        this.subRef = value;
    }

}
