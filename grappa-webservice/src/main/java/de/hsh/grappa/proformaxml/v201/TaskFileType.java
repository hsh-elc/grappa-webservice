
package de.hsh.grappa.proformaxml.v201;

import javax.xml.bind.annotation.*;


/**
 * <p>Java-Klasse f�r task-file-type complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="task-file-type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;group ref="{urn:proforma:v2.0.1}file-choice-group"/>
 *         &lt;element name="internal-description" type="{urn:proforma:v2.0.1}description-type" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="mimetype" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="used-by-grader" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="visible" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="yes"/>
 *             &lt;enumeration value="no"/>
 *             &lt;enumeration value="delayed"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="usage-by-lms" default="download">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="edit"/>
 *             &lt;enumeration value="display"/>
 *             &lt;enumeration value="download"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "task-file-type", namespace = "urn:proforma:v2.0.1", propOrder = {
    "embeddedBinFile",
    "embeddedTxtFile",
    "attachedBinFile",
    "attachedTxtFile",
    "internalDescription"
})
public class TaskFileType {

    @XmlElement(name = "embedded-bin-file", namespace = "urn:proforma:v2.0.1")
    protected EmbeddedBinFileType embeddedBinFile;
    @XmlElement(name = "embedded-txt-file", namespace = "urn:proforma:v2.0.1")
    protected EmbeddedTxtFileType embeddedTxtFile;
    @XmlElement(name = "attached-bin-file", namespace = "urn:proforma:v2.0.1")
    protected String attachedBinFile;
    @XmlElement(name = "attached-txt-file", namespace = "urn:proforma:v2.0.1")
    protected AttachedTxtFileType attachedTxtFile;
    @XmlElement(name = "internal-description", namespace = "urn:proforma:v2.0.1")
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
     * Ruft den Wert der embeddedBinFile-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link EmbeddedBinFileType }
     *     
     */
    public EmbeddedBinFileType getEmbeddedBinFile() {
        return embeddedBinFile;
    }

    /**
     * Legt den Wert der embeddedBinFile-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link EmbeddedBinFileType }
     *     
     */
    public void setEmbeddedBinFile(EmbeddedBinFileType value) {
        this.embeddedBinFile = value;
    }

    /**
     * Ruft den Wert der embeddedTxtFile-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link EmbeddedTxtFileType }
     *     
     */
    public EmbeddedTxtFileType getEmbeddedTxtFile() {
        return embeddedTxtFile;
    }

    /**
     * Legt den Wert der embeddedTxtFile-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link EmbeddedTxtFileType }
     *     
     */
    public void setEmbeddedTxtFile(EmbeddedTxtFileType value) {
        this.embeddedTxtFile = value;
    }

    /**
     * Ruft den Wert der attachedBinFile-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAttachedBinFile() {
        return attachedBinFile;
    }

    /**
     * Legt den Wert der attachedBinFile-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAttachedBinFile(String value) {
        this.attachedBinFile = value;
    }

    /**
     * Ruft den Wert der attachedTxtFile-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link AttachedTxtFileType }
     *     
     */
    public AttachedTxtFileType getAttachedTxtFile() {
        return attachedTxtFile;
    }

    /**
     * Legt den Wert der attachedTxtFile-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link AttachedTxtFileType }
     *     
     */
    public void setAttachedTxtFile(AttachedTxtFileType value) {
        this.attachedTxtFile = value;
    }

    /**
     * Ruft den Wert der internalDescription-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInternalDescription() {
        return internalDescription;
    }

    /**
     * Legt den Wert der internalDescription-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInternalDescription(String value) {
        this.internalDescription = value;
    }

    /**
     * Ruft den Wert der id-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Legt den Wert der id-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Ruft den Wert der mimetype-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMimetype() {
        return mimetype;
    }

    /**
     * Legt den Wert der mimetype-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMimetype(String value) {
        this.mimetype = value;
    }

    /**
     * Ruft den Wert der usedByGrader-Eigenschaft ab.
     * 
     */
    public boolean isUsedByGrader() {
        return usedByGrader;
    }

    /**
     * Legt den Wert der usedByGrader-Eigenschaft fest.
     * 
     */
    public void setUsedByGrader(boolean value) {
        this.usedByGrader = value;
    }

    /**
     * Ruft den Wert der visible-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVisible() {
        return visible;
    }

    /**
     * Legt den Wert der visible-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVisible(String value) {
        this.visible = value;
    }

    /**
     * Ruft den Wert der usageByLms-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUsageByLms() {
        if (usageByLms == null) {
            return "download";
        } else {
            return usageByLms;
        }
    }

    /**
     * Legt den Wert der usageByLms-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUsageByLms(String value) {
        this.usageByLms = value;
    }

}
