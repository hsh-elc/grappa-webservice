
package proforma.xml21;

import javax.xml.bind.annotation.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for submission-restrictions-type complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="submission-restrictions-type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="file-restriction" type="{urn:proforma:v2.1}file-restr-type" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="description" type="{urn:proforma:v2.1}description-type" minOccurs="0"/>
 *         &lt;element name="internal-description" type="{urn:proforma:v2.1}description-type" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="max-size" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "submission-restrictions-type", namespace = "urn:proforma:v2.1", propOrder = {
    "fileRestriction",
    "description",
    "internalDescription"
})
public class SubmissionRestrictionsType {

    @XmlElement(name = "file-restriction", namespace = "urn:proforma:v2.1")
    protected List<FileRestrType> fileRestriction;
    @XmlElement(namespace = "urn:proforma:v2.1")
    protected String description;
    @XmlElement(name = "internal-description", namespace = "urn:proforma:v2.1")
    protected String internalDescription;
    @XmlAttribute(name = "max-size")
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger maxSize;

    /**
     * Gets the value of the fileRestriction property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the fileRestriction property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFileRestriction().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FileRestrType }
     */
    public List<FileRestrType> getFileRestriction() {
        if (fileRestriction == null) {
            fileRestriction = new ArrayList<FileRestrType>();
        }
        return this.fileRestriction;
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
     * Gets the value of the maxSize property.
     *
     * @return possible object is
     * {@link BigInteger }
     */
    public BigInteger getMaxSize() {
        return maxSize;
    }

    /**
     * Sets the value of the maxSize property.
     *
     * @param value allowed object is
     *              {@link BigInteger }
     */
    public void setMaxSize(BigInteger value) {
        this.maxSize = value;
    }

}
