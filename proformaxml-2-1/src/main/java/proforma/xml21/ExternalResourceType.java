
package proforma.xml21;

import org.w3c.dom.Element;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for external-resource-type complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="external-resource-type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="internal-description" type="{urn:proforma:v2.1}description-type" minOccurs="0"/>
 *         &lt;any processContents='lax' namespace='##other' maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{urn:proforma:v2.1}resource-properties"/>
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="reference" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "external-resource-type", namespace = "urn:proforma:v2.1", propOrder = {
    "internalDescription",
    "any"
})
public class ExternalResourceType {

    @XmlElement(name = "internal-description", namespace = "urn:proforma:v2.1")
    protected String internalDescription;
    @XmlAnyElement(lax = true)
    protected List<Object> any;
    @XmlAttribute(name = "id", required = true)
    protected String id;
    @XmlAttribute(name = "reference")
    protected String reference;
    @XmlAttribute(name = "used-by-grader", required = true)
    protected boolean usedByGrader;
    @XmlAttribute(name = "visible", required = true)
    protected String visible;
    @XmlAttribute(name = "usage-by-lms")
    protected String usageByLms;

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
     * Gets the value of the any property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the any property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAny().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * {@link Element }
     */
    public List<Object> getAny() {
        if (any == null) {
            any = new ArrayList<Object>();
        }
        return this.any;
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

    /**
     * Gets the value of the reference property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getReference() {
        return reference;
    }

    /**
     * Sets the value of the reference property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setReference(String value) {
        this.reference = value;
    }

    /**
     * Gets the value of the usedByGrader property.
     */
    public boolean isUsedByGrader() {
        return usedByGrader;
    }

    /**
     * Sets the value of the usedByGrader property.
     */
    public void setUsedByGrader(boolean value) {
        this.usedByGrader = value;
    }

    /**
     * Gets the value of the visible property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getVisible() {
        return visible;
    }

    /**
     * Sets the value of the visible property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setVisible(String value) {
        this.visible = value;
    }

    /**
     * Gets the value of the usageByLms property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getUsageByLms() {
        if (usageByLms == null) {
            return "download";
        } else {
            return usageByLms;
        }
    }

    /**
     * Sets the value of the usageByLms property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setUsageByLms(String value) {
        this.usageByLms = value;
    }

}
