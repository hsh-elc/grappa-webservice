
package proforma.xml21;

import javax.xml.bind.annotation.*;


/**
 * Inner nodes of the grading scheme hierarchy carry pointers to children. This
 * element represents such a pointer. There are two kinds of pointers: "test-ref" pointers and "combine-ref"
 * pointers.
 *
 *
 * <p>Java class for grades-base-ref-child-type complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="grades-base-ref-child-type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice minOccurs="0">
 *           &lt;element name="nullify-conditions" type="{urn:proforma:v2.1}grades-nullify-conditions-type"/>
 *           &lt;element name="nullify-condition" type="{urn:proforma:v2.1}grades-nullify-condition-type"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *       &lt;attribute name="weight" type="{http://www.w3.org/2001/XMLSchema}double" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "grades-base-ref-child-type", namespace = "urn:proforma:v2.1", propOrder = {
    "nullifyConditions",
    "nullifyCondition"
})
@XmlSeeAlso({
    GradesTestRefChildType.class,
    GradesCombineRefChildType.class
})
public class GradesBaseRefChildType {

    @XmlElement(name = "nullify-conditions", namespace = "urn:proforma:v2.1")
    protected GradesNullifyConditionsType nullifyConditions;
    @XmlElement(name = "nullify-condition", namespace = "urn:proforma:v2.1")
    protected GradesNullifyConditionType nullifyCondition;
    @XmlAttribute(name = "weight")
    protected Double weight;

    /**
     * Gets the value of the nullifyConditions property.
     *
     * @return possible object is
     * {@link GradesNullifyConditionsType }
     */
    public GradesNullifyConditionsType getNullifyConditions() {
        return nullifyConditions;
    }

    /**
     * Sets the value of the nullifyConditions property.
     *
     * @param value allowed object is
     *              {@link GradesNullifyConditionsType }
     */
    public void setNullifyConditions(GradesNullifyConditionsType value) {
        this.nullifyConditions = value;
    }

    /**
     * Gets the value of the nullifyCondition property.
     *
     * @return possible object is
     * {@link GradesNullifyConditionType }
     */
    public GradesNullifyConditionType getNullifyCondition() {
        return nullifyCondition;
    }

    /**
     * Sets the value of the nullifyCondition property.
     *
     * @param value allowed object is
     *              {@link GradesNullifyConditionType }
     */
    public void setNullifyCondition(GradesNullifyConditionType value) {
        this.nullifyCondition = value;
    }

    /**
     * Gets the value of the weight property.
     *
     * @return possible object is
     * {@link Double }
     */
    public Double getWeight() {
        return weight;
    }

    /**
     * Sets the value of the weight property.
     *
     * @param value allowed object is
     *              {@link Double }
     */
    public void setWeight(Double value) {
        this.weight = value;
    }

}
