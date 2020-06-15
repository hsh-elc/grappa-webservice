
package de.hsh.grappa.proformaxml.v300;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * A "combine-ref" node points to a "combine" node in the grading scheme hierarchy.
 *         As such the result of the pointed at node is obtained and included in a bottom-up fashion in the calculation of
 *         the total result.
 *       
 * 
 * <p>Java-Klasse für grades-combine-ref-child-type complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="grades-combine-ref-child-type">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:proforma:v3.0.0}grades-base-ref-child-type">
 *       &lt;attribute name="ref" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "grades-combine-ref-child-type", namespace = "urn:proforma:v3.0.0")
public class GradesCombineRefChildType
    extends GradesBaseRefChildType
{

    @XmlAttribute(name = "ref", required = true)
    protected String ref;

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

}
