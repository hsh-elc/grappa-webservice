
package de.hsh.grappa.proformaxml.v300;

import javax.xml.bind.annotation.*;


/**
 * <p>Java-Klasse f�r submission-file-type complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="submission-file-type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;group ref="{urn:proforma:v3.0.0}file-choice-group"/>
 *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="mimetype" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "submission-file-type", namespace = "urn:proforma:v3.0.0", propOrder = {
    "embeddedBinFile",
    "embeddedTxtFile",
    "attachedBinFile",
    "attachedTxtFile"
})
public class SubmissionFileType {

    @XmlElement(name = "embedded-bin-file", namespace = "urn:proforma:v3.0.0")
    protected EmbeddedBinFileType embeddedBinFile;
    @XmlElement(name = "embedded-txt-file", namespace = "urn:proforma:v3.0.0")
    protected EmbeddedTxtFileType embeddedTxtFile;
    @XmlElement(name = "attached-bin-file", namespace = "urn:proforma:v3.0.0")
    protected String attachedBinFile;
    @XmlElement(name = "attached-txt-file", namespace = "urn:proforma:v3.0.0")
    protected AttachedTxtFileType attachedTxtFile;
    @XmlAttribute(name = "id")
    protected String id;
    @XmlAttribute(name = "mimetype")
    protected String mimetype;

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

}
