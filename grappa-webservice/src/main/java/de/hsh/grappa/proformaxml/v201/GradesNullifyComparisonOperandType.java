
package de.hsh.grappa.proformaxml.v201;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * Specifies an operand of a comparison nullify condition.
 * 
 * <p>Java-Klasse f�r grades-nullify-comparison-operand-type complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="grades-nullify-comparison-operand-type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "grades-nullify-comparison-operand-type", namespace = "urn:proforma:v2.0.1")
@XmlSeeAlso({
    GradesNullifyTestRefType.class,
    GradesNullifyLiteralType.class,
    GradesNullifyCombineRefType.class
})
public class GradesNullifyComparisonOperandType {


}
