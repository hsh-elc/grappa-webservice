
package de.hsh.grappa.proformaxml.v300;

import javax.xml.bind.annotation.*;


/**
 * Inner nodes of the grading scheme hierarchy carry pointers to children. This
 *         element represents such a pointer. There are two kinds of pointers: "test-ref" pointers and "combine-ref"
 *         pointers.
 *       
 * 
 * <p>Java-Klasse f�r grades-base-ref-child-type complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="grades-base-ref-child-type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice minOccurs="0">
 *           &lt;element name="nullify-conditions" type="{urn:proforma:v3.0.0}grades-nullify-conditions-type"/>
 *           &lt;element name="nullify-condition" type="{urn:proforma:v3.0.0}grades-nullify-condition-type"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *       &lt;attribute name="weight" type="{http://www.w3.org/2001/XMLSchema}double" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "grades-base-ref-child-type", namespace = "urn:proforma:v3.0.0", propOrder = {
    "nullifyConditions",
    "nullifyCondition"
})
@XmlSeeAlso({
    GradesTestRefChildType.class,
    GradesCombineRefChildType.class
})
public class GradesBaseRefChildType {

    @XmlElement(name = "nullify-conditions", namespace = "urn:proforma:v3.0.0")
    protected GradesNullifyConditionsType nullifyConditions;
    @XmlElement(name = "nullify-condition", namespace = "urn:proforma:v3.0.0")
    protected GradesNullifyConditionType nullifyCondition;
    @XmlAttribute(name = "weight")
    protected Double weight;

    /**
     * Ruft den Wert der nullifyConditions-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link GradesNullifyConditionsType }
     *     
     */
    public GradesNullifyConditionsType getNullifyConditions() {
        return nullifyConditions;
    }

    /**
     * Legt den Wert der nullifyConditions-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link GradesNullifyConditionsType }
     *     
     */
    public void setNullifyConditions(GradesNullifyConditionsType value) {
        this.nullifyConditions = value;
    }

    /**
     * Ruft den Wert der nullifyCondition-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link GradesNullifyConditionType }
     *     
     */
    public GradesNullifyConditionType getNullifyCondition() {
        return nullifyCondition;
    }

    /**
     * Legt den Wert der nullifyCondition-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link GradesNullifyConditionType }
     *     
     */
    public void setNullifyCondition(GradesNullifyConditionType value) {
        this.nullifyCondition = value;
    }

    /**
     * Ruft den Wert der weight-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getWeight() {
        return weight;
    }

    /**
     * Legt den Wert der weight-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setWeight(Double value) {
        this.weight = value;
    }

}
