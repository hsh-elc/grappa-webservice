
package de.hsh.grappa.proformaxml.v300;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * An operand of a comparison expression pointing to a "test".
 * 
 * <p>Java-Klasse für grades-nullify-test-ref-type complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="grades-nullify-test-ref-type">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:proforma:v3.0.0}grades-nullify-comparison-operand-type">
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
@XmlType(name = "grades-nullify-test-ref-type", namespace = "urn:proforma:v3.0.0")
public class GradesNullifyTestRefType
    extends GradesNullifyComparisonOperandType
{

    @XmlAttribute(name = "ref", required = true)
    protected String ref;
    @XmlAttribute(name = "sub-ref")
    protected String subRef;

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
