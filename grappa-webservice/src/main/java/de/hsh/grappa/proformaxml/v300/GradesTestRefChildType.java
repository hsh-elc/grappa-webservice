
package de.hsh.grappa.proformaxml.v300;

import javax.xml.bind.annotation.*;


/**
 * A "test-ref" node points to a test in a ProFormA task. As such the result of the
 *         pointed at test is obtained and included in a bottom-up fashion in the calculation of the total result.
 *       
 * 
 * <p>Java-Klasse f�r grades-test-ref-child-type complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="grades-test-ref-child-type">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:proforma:v3.0.0}grades-base-ref-child-type">
 *       &lt;sequence>
 *         &lt;element name="title" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="description" type="{urn:proforma:v3.0.0}description-type" minOccurs="0"/>
 *         &lt;element name="internal-description" type="{urn:proforma:v3.0.0}description-type" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="ref" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="sub-ref" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "grades-test-ref-child-type", namespace = "urn:proforma:v3.0.0", propOrder = {
    "title",
    "description",
    "internalDescription"
})
public class GradesTestRefChildType
    extends GradesBaseRefChildType
{

    @XmlElement(namespace = "urn:proforma:v3.0.0")
    protected String title;
    @XmlElement(namespace = "urn:proforma:v3.0.0")
    protected String description;
    @XmlElement(name = "internal-description", namespace = "urn:proforma:v3.0.0")
    protected String internalDescription;
    @XmlAttribute(name = "ref", required = true)
    protected String ref;
    @XmlAttribute(name = "sub-ref")
    protected String subRef;

    /**
     * Ruft den Wert der title-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTitle() {
        return title;
    }

    /**
     * Legt den Wert der title-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTitle(String value) {
        this.title = value;
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
     * Ruft den Wert der ref-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRef() {
        return ref;
    }

    /**
     * Legt den Wert der ref-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRef(String value) {
        this.ref = value;
    }

    /**
     * Ruft den Wert der subRef-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSubRef() {
        return subRef;
    }

    /**
     * Legt den Wert der subRef-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSubRef(String value) {
        this.subRef = value;
    }

}
