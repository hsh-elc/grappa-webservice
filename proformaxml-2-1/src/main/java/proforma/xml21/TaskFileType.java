
package proforma.xml21;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for task-file-type complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="task-file-type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;group ref="{urn:proforma:v2.1}file-choice-group"/>
 *         &lt;element name="internal-description" type="{urn:proforma:v2.1}description-type" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{urn:proforma:v2.1}resource-properties"/>
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="mimetype" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "task-file-type", namespace = "urn:proforma:v2.1", propOrder = {
    "embeddedBinFile",
    "embeddedTxtFile",
    "attachedBinFile",
    "attachedTxtFile",
    "internalDescription"
})
public class TaskFileType {

    @XmlElement(name = "embedded-bin-file", namespace = "urn:proforma:v2.1")
    protected EmbeddedBinFileType embeddedBinFile;
    @XmlElement(name = "embedded-txt-file", namespace = "urn:proforma:v2.1")
    protected EmbeddedTxtFileType embeddedTxtFile;
    @XmlElement(name = "attached-bin-file", namespace = "urn:proforma:v2.1")
    protected String attachedBinFile;
    @XmlElement(name = "attached-txt-file", namespace = "urn:proforma:v2.1")
    protected AttachedTxtFileType attachedTxtFile;
    @XmlElement(name = "internal-description", namespace = "urn:proforma:v2.1")
    protected String internalDescription;
    @XmlAttribute(name = "id", required = true)
    protected String id;
    @XmlAttribute(name = "mimetype")
    protected String mimetype;
    @XmlAttribute(name = "used-by-grader", required = true)
    protected boolean usedByGrader;
    @XmlAttribute(name = "visible", required = true)
    protected String visible;
    @XmlAttribute(name = "usage-by-lms")
    protected String usageByLms;

    /**
     * Gets the value of the embeddedBinFile property.
     *
     * @return possible object is
     * {@link EmbeddedBinFileType }
     */
    public EmbeddedBinFileType getEmbeddedBinFile() {
        return embeddedBinFile;
    }

    /**
     * Sets the value of the embeddedBinFile property.
     *
     * @param value allowed object is
     *              {@link EmbeddedBinFileType }
     */
    public void setEmbeddedBinFile(EmbeddedBinFileType value) {
        this.embeddedBinFile = value;
    }

    /**
     * Gets the value of the embeddedTxtFile property.
     *
     * @return possible object is
     * {@link EmbeddedTxtFileType }
     */
    public EmbeddedTxtFileType getEmbeddedTxtFile() {
        return embeddedTxtFile;
    }

    /**
     * Sets the value of the embeddedTxtFile property.
     *
     * @param value allowed object is
     *              {@link EmbeddedTxtFileType }
     */
    public void setEmbeddedTxtFile(EmbeddedTxtFileType value) {
        this.embeddedTxtFile = value;
    }

    /**
     * Gets the value of the attachedBinFile property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getAttachedBinFile() {
        return attachedBinFile;
    }

    /**
     * Sets the value of the attachedBinFile property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setAttachedBinFile(String value) {
        this.attachedBinFile = value;
    }

    /**
     * Gets the value of the attachedTxtFile property.
     *
     * @return possible object is
     * {@link AttachedTxtFileType }
     */
    public AttachedTxtFileType getAttachedTxtFile() {
        return attachedTxtFile;
    }

    /**
     * Sets the value of the attachedTxtFile property.
     *
     * @param value allowed object is
     *              {@link AttachedTxtFileType }
     */
    public void setAttachedTxtFile(AttachedTxtFileType value) {
        this.attachedTxtFile = value;
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

    /**
     * Gets the value of the mimetype property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getMimetype() {
        return mimetype;
    }

    /**
     * Sets the value of the mimetype property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setMimetype(String value) {
        this.mimetype = value;
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
