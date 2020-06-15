
package de.hsh.grappa.proformaxml.v201;

import javax.xml.bind.annotation.*;


/**
 * <p>Java-Klasse f�r model-solution-type complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="model-solution-type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="filerefs" type="{urn:proforma:v2.0.1}filerefs-type"/>
 *         &lt;element name="description" type="{urn:proforma:v2.0.1}description-type" minOccurs="0"/>
 *         &lt;element name="internal-description" type="{urn:proforma:v2.0.1}description-type" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "model-solution-type", namespace = "urn:proforma:v2.0.1", propOrder = {
    "filerefs",
    "description",
    "internalDescription"
})
public class ModelSolutionType {

    @XmlElement(namespace = "urn:proforma:v2.0.1", required = true)
    protected FilerefsType filerefs;
    @XmlElement(namespace = "urn:proforma:v2.0.1")
    protected String description;
    @XmlElement(name = "internal-description", namespace = "urn:proforma:v2.0.1")
    protected String internalDescription;
    @XmlAttribute(name = "id", required = true)
    protected String id;

    /**
     * Ruft den Wert der filerefs-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link FilerefsType }
     *     
     */
    public FilerefsType getFilerefs() {
        return filerefs;
    }

    /**
     * Legt den Wert der filerefs-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link FilerefsType }
     *     
     */
    public void setFilerefs(FilerefsType value) {
        this.filerefs = value;
    }

    /**
     * Ruft den Wert der description-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Legt den Wert der description-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
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

}
