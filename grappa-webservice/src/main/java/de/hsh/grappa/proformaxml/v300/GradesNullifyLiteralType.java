
package de.hsh.grappa.proformaxml.v300;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import java.math.BigDecimal;


/**
 * A numerical constant serving as an operand of the comparison expression.
 *       
 * 
 * <p>Java-Klasse f�r grades-nullify-literal-type complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="grades-nullify-literal-type">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:proforma:v3.0.0}grades-nullify-comparison-operand-type">
 *       &lt;attribute name="value" use="required" type="{http://www.w3.org/2001/XMLSchema}decimal" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "grades-nullify-literal-type", namespace = "urn:proforma:v3.0.0")
public class GradesNullifyLiteralType
    extends GradesNullifyComparisonOperandType
{

    @XmlAttribute(name = "value", required = true)
    protected BigDecimal value;

    /**
     * Ruft den Wert der value-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getValue() {
        return value;
    }

    /**
     * Legt den Wert der value-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setValue(BigDecimal value) {
        this.value = value;
    }

}
