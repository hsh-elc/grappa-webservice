
package proforma.xml21;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for model-solution-type complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="model-solution-type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="filerefs" type="{urn:proforma:v2.1}filerefs-type"/>
 *         &lt;element name="description" type="{urn:proforma:v2.1}description-type" minOccurs="0"/>
 *         &lt;element name="internal-description" type="{urn:proforma:v2.1}description-type" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "model-solution-type", namespace = "urn:proforma:v2.1", propOrder = {
    "filerefs",
    "description",
    "internalDescription"
})
public class ModelSolutionType {

    @XmlElement(namespace = "urn:proforma:v2.1", required = true)
    protected FilerefsType filerefs;
    @XmlElement(namespace = "urn:proforma:v2.1")
    protected String description;
    @XmlElement(name = "internal-description", namespace = "urn:proforma:v2.1")
    protected String internalDescription;
    @XmlAttribute(name = "id", required = true)
    protected String id;

    /**
     * Gets the value of the filerefs property.
     *
     * @return possible object is
     * {@link FilerefsType }
     */
    public FilerefsType getFilerefs() {
        return filerefs;
    }

    /**
     * Sets the value of the filerefs property.
     *
     * @param value allowed object is
     *              {@link FilerefsType }
     */
    public void setFilerefs(FilerefsType value) {
        this.filerefs = value;
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
     * Gets the value of the id property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setId(String value) {
        this.id = value;
    }

}
