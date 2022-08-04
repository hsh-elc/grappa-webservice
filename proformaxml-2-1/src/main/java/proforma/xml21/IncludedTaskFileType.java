
package proforma.xml21;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for included-task-file-type complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="included-task-file-type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element name="embedded-zip-file" type="{urn:proforma:v2.1}embedded-bin-file-type"/>
 *         &lt;element name="embedded-xml-file" type="{urn:proforma:v2.1}embedded-bin-file-type"/>
 *         &lt;element name="attached-zip-file" type="{urn:proforma:v2.1}attached-bin-file-type"/>
 *         &lt;element name="attached-xml-file" type="{urn:proforma:v2.1}attached-txt-file-type"/>
 *       &lt;/choice>
 *       &lt;attribute name="uuid" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "included-task-file-type", namespace = "urn:proforma:v2.1", propOrder = {
    "embeddedZipFile",
    "embeddedXmlFile",
    "attachedZipFile",
    "attachedXmlFile"
})
public class IncludedTaskFileType {

    @XmlElement(name = "embedded-zip-file", namespace = "urn:proforma:v2.1")
    protected EmbeddedBinFileType embeddedZipFile;
    @XmlElement(name = "embedded-xml-file", namespace = "urn:proforma:v2.1")
    protected EmbeddedBinFileType embeddedXmlFile;
    @XmlElement(name = "attached-zip-file", namespace = "urn:proforma:v2.1")
    protected String attachedZipFile;
    @XmlElement(name = "attached-xml-file", namespace = "urn:proforma:v2.1")
    protected AttachedTxtFileType attachedXmlFile;
    @XmlAttribute(name = "uuid")
    protected String uuid;

    /**
     * Gets the value of the embeddedZipFile property.
     *
     * @return possible object is
     * {@link EmbeddedBinFileType }
     */
    public EmbeddedBinFileType getEmbeddedZipFile() {
        return embeddedZipFile;
    }

    /**
     * Sets the value of the embeddedZipFile property.
     *
     * @param value allowed object is
     *              {@link EmbeddedBinFileType }
     */
    public void setEmbeddedZipFile(EmbeddedBinFileType value) {
        this.embeddedZipFile = value;
    }

    /**
     * Gets the value of the embeddedXmlFile property.
     *
     * @return possible object is
     * {@link EmbeddedBinFileType }
     */
    public EmbeddedBinFileType getEmbeddedXmlFile() {
        return embeddedXmlFile;
    }

    /**
     * Sets the value of the embeddedXmlFile property.
     *
     * @param value allowed object is
     *              {@link EmbeddedBinFileType }
     */
    public void setEmbeddedXmlFile(EmbeddedBinFileType value) {
        this.embeddedXmlFile = value;
    }

    /**
     * Gets the value of the attachedZipFile property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getAttachedZipFile() {
        return attachedZipFile;
    }

    /**
     * Sets the value of the attachedZipFile property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setAttachedZipFile(String value) {
        this.attachedZipFile = value;
    }

    /**
     * Gets the value of the attachedXmlFile property.
     *
     * @return possible object is
     * {@link AttachedTxtFileType }
     */
    public AttachedTxtFileType getAttachedXmlFile() {
        return attachedXmlFile;
    }

    /**
     * Sets the value of the attachedXmlFile property.
     *
     * @param value allowed object is
     *              {@link AttachedTxtFileType }
     */
    public void setAttachedXmlFile(AttachedTxtFileType value) {
        this.attachedXmlFile = value;
    }

    /**
     * Gets the value of the uuid property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Sets the value of the uuid property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setUuid(String value) {
        this.uuid = value;
    }

}
