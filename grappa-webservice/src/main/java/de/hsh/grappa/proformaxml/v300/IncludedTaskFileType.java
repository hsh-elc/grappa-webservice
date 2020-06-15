
package de.hsh.grappa.proformaxml.v300;

import javax.xml.bind.annotation.*;


/**
 * <p>Java-Klasse f�r included-task-file-type complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="included-task-file-type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element name="embedded-zip-file" type="{urn:proforma:v3.0.0}embedded-bin-file-type"/>
 *         &lt;element name="attached-zip-file" type="{urn:proforma:v3.0.0}attached-bin-file-type"/>
 *         &lt;element name="attached-xml-file" type="{urn:proforma:v3.0.0}attached-txt-file-type"/>
 *       &lt;/choice>
 *       &lt;attribute name="uuid" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "included-task-file-type", namespace = "urn:proforma:v3.0.0", propOrder = {
    "embeddedZipFile",
    "attachedZipFile",
    "attachedXmlFile"
})
public class IncludedTaskFileType {

    @XmlElement(name = "embedded-zip-file", namespace = "urn:proforma:v3.0.0")
    protected EmbeddedBinFileType embeddedZipFile;
    @XmlElement(name = "attached-zip-file", namespace = "urn:proforma:v3.0.0")
    protected String attachedZipFile;
    @XmlElement(name = "attached-xml-file", namespace = "urn:proforma:v3.0.0")
    protected AttachedTxtFileType attachedXmlFile;
    @XmlAttribute(name = "uuid")
    protected String uuid;

    /**
     * Ruft den Wert der embeddedZipFile-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link EmbeddedBinFileType }
     *     
     */
    public EmbeddedBinFileType getEmbeddedZipFile() {
        return embeddedZipFile;
    }

    /**
     * Legt den Wert der embeddedZipFile-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link EmbeddedBinFileType }
     *     
     */
    public void setEmbeddedZipFile(EmbeddedBinFileType value) {
        this.embeddedZipFile = value;
    }

    /**
     * Ruft den Wert der attachedZipFile-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAttachedZipFile() {
        return attachedZipFile;
    }

    /**
     * Legt den Wert der attachedZipFile-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAttachedZipFile(String value) {
        this.attachedZipFile = value;
    }

    /**
     * Ruft den Wert der attachedXmlFile-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link AttachedTxtFileType }
     *     
     */
    public AttachedTxtFileType getAttachedXmlFile() {
        return attachedXmlFile;
    }

    /**
     * Legt den Wert der attachedXmlFile-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link AttachedTxtFileType }
     *     
     */
    public void setAttachedXmlFile(AttachedTxtFileType value) {
        this.attachedXmlFile = value;
    }

    /**
     * Ruft den Wert der uuid-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Legt den Wert der uuid-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUuid(String value) {
        this.uuid = value;
    }

}
