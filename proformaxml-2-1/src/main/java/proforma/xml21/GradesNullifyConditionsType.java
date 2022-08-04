
package proforma.xml21;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


/**
 * Specifies a composite condition when the sub result of a pointed-at node should
 * get nullified. The composite condition is attributed with one of the boolean operators { and, or }. Further it
 * contains operands that usually are of the nullify-condition type, which represents a simple comparison.
 * Nevertheless a composite condition can have nested composite conditions as operands as well.
 *
 *
 * <p>Java class for grades-nullify-conditions-type complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="grades-nullify-conditions-type">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:proforma:v2.1}grades-nullify-base-type">
 *       &lt;sequence>
 *         &lt;choice maxOccurs="unbounded" minOccurs="2">
 *           &lt;element name="nullify-conditions" type="{urn:proforma:v2.1}grades-nullify-conditions-type"/>
 *           &lt;element name="nullify-condition" type="{urn:proforma:v2.1}grades-nullify-condition-type"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *       &lt;attribute name="compose-op" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="and"/>
 *             &lt;enumeration value="or"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "grades-nullify-conditions-type", namespace = "urn:proforma:v2.1", propOrder = {
    "nullifyConditionsOrNullifyCondition"
})
public class GradesNullifyConditionsType
    extends GradesNullifyBaseType {

    @XmlElements({
        @XmlElement(name = "nullify-conditions", namespace = "urn:proforma:v2.1", type = GradesNullifyConditionsType.class),
        @XmlElement(name = "nullify-condition", namespace = "urn:proforma:v2.1", type = GradesNullifyConditionType.class)
    })
    protected List<GradesNullifyBaseType> nullifyConditionsOrNullifyCondition;
    @XmlAttribute(name = "compose-op", required = true)
    protected String composeOp;

    /**
     * Gets the value of the nullifyConditionsOrNullifyCondition property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the nullifyConditionsOrNullifyCondition property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNullifyConditionsOrNullifyCondition().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link GradesNullifyConditionsType }
     * {@link GradesNullifyConditionType }
     */
    public List<GradesNullifyBaseType> getNullifyConditionsOrNullifyCondition() {
        if (nullifyConditionsOrNullifyCondition == null) {
            nullifyConditionsOrNullifyCondition = new ArrayList<GradesNullifyBaseType>();
        }
        return this.nullifyConditionsOrNullifyCondition;
    }

    /**
     * Gets the value of the composeOp property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getComposeOp() {
        return composeOp;
    }

    /**
     * Sets the value of the composeOp property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setComposeOp(String value) {
        this.composeOp = value;
    }

}
