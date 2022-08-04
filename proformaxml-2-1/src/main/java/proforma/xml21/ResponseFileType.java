
package proforma.xml21;

import javax.xml.bind.annotation.*;

/**
 * <p>Java class for response-file-type complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="response-file-type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;group ref="{urn:proforma:v2.1}file-choice-group"/>
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="mimetype" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="title" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "response-file-type", namespace = "urn:proforma:v2.1", propOrder = {
    "embeddedBinFile",
    "embeddedTxtFile",
    "attachedBinFile",
    "attachedTxtFile"
})
public class ResponseFileType {

    @XmlElement(name = "embedded-bin-file", namespace = "urn:proforma:v2.1")
    protected EmbeddedBinFileType embeddedBinFile;
    @XmlElement(name = "embedded-txt-file", namespace = "urn:proforma:v2.1")
    protected EmbeddedTxtFileType embeddedTxtFile;
    @XmlElement(name = "attached-bin-file", namespace = "urn:proforma:v2.1")
    protected String attachedBinFile;
    @XmlElement(name = "attached-txt-file", namespace = "urn:proforma:v2.1")
    protected AttachedTxtFileType attachedTxtFile;
    @XmlAttribute(name = "id", required = true)
    protected String id;
    @XmlAttribute(name = "mimetype")
    protected String mimetype;
    @XmlAttribute(name = "title", required = true)
    protected String title;

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

}
