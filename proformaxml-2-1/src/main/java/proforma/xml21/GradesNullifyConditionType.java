
package proforma.xml21;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


/**
 * Specifies a simple comparison condition when the sub result of a pointed-at node
 * should get nullified. This simple comparison condition is attributed with one of the six common comparison
 * operators. Further it contains operands that refer to tests or combine nodes or that specify a numerical
 * constant, which a result should be compared to.
 *
 *
 * <p>Java class for grades-nullify-condition-type complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="grades-nullify-condition-type">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:proforma:v2.1}grades-nullify-base-type">
 *       &lt;sequence>
 *         &lt;choice maxOccurs="2" minOccurs="2">
 *           &lt;element name="nullify-combine-ref" type="{urn:proforma:v2.1}grades-nullify-combine-ref-type"/>
 *           &lt;element name="nullify-test-ref" type="{urn:proforma:v2.1}grades-nullify-test-ref-type"/>
 *           &lt;element name="nullify-literal" type="{urn:proforma:v2.1}grades-nullify-literal-type"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *       &lt;attribute name="compare-op" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="eq"/>
 *             &lt;enumeration value="ne"/>
 *             &lt;enumeration value="gt"/>
 *             &lt;enumeration value="ge"/>
 *             &lt;enumeration value="lt"/>
 *             &lt;enumeration value="le"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "grades-nullify-condition-type", namespace = "urn:proforma:v2.1", propOrder = {
    "nullifyCombineRefOrNullifyTestRefOrNullifyLiteral"
})
public class GradesNullifyConditionType
    extends GradesNullifyBaseType {

    @XmlElements({
        @XmlElement(name = "nullify-combine-ref", namespace = "urn:proforma:v2.1", type = GradesNullifyCombineRefType.class),
        @XmlElement(name = "nullify-test-ref", namespace = "urn:proforma:v2.1", type = GradesNullifyTestRefType.class),
        @XmlElement(name = "nullify-literal", namespace = "urn:proforma:v2.1", type = GradesNullifyLiteralType.class)
    })
    protected List<GradesNullifyComparisonOperandType> nullifyCombineRefOrNullifyTestRefOrNullifyLiteral;
    @XmlAttribute(name = "compare-op", required = true)
    protected String compareOp;

    /**
     * Gets the value of the nullifyCombineRefOrNullifyTestRefOrNullifyLiteral property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the nullifyCombineRefOrNullifyTestRefOrNullifyLiteral property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNullifyCombineRefOrNullifyTestRefOrNullifyLiteral().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link GradesNullifyCombineRefType }
     * {@link GradesNullifyTestRefType }
     * {@link GradesNullifyLiteralType }
     */
    public List<GradesNullifyComparisonOperandType> getNullifyCombineRefOrNullifyTestRefOrNullifyLiteral() {
        if (nullifyCombineRefOrNullifyTestRefOrNullifyLiteral == null) {
            nullifyCombineRefOrNullifyTestRefOrNullifyLiteral = new ArrayList<GradesNullifyComparisonOperandType>();
        }
        return this.nullifyCombineRefOrNullifyTestRefOrNullifyLiteral;
    }

    /**
     * Gets the value of the compareOp property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getCompareOp() {
        return compareOp;
    }

    /**
     * Sets the value of the compareOp property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setCompareOp(String value) {
        this.compareOp = value;
    }

}
