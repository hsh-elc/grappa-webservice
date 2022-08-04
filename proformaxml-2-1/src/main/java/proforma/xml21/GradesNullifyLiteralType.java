
package proforma.xml21;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import java.math.BigDecimal;


/**
 * A numerical constant serving as an operand of the comparison expression.
 *
 *
 * <p>Java class for grades-nullify-literal-type complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="grades-nullify-literal-type">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:proforma:v2.1}grades-nullify-comparison-operand-type">
 *       &lt;attribute name="value" use="required" type="{http://www.w3.org/2001/XMLSchema}decimal" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "grades-nullify-literal-type", namespace = "urn:proforma:v2.1")
public class GradesNullifyLiteralType
    extends GradesNullifyComparisonOperandType {

    @XmlAttribute(name = "value", required = true)
    protected BigDecimal value;

    /**
     * Gets the value of the value property.
     *
     * @return possible object is
     * {@link BigDecimal }
     */
    public BigDecimal getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     *
     * @param value allowed object is
     *              {@link BigDecimal }
     */
    public void setValue(BigDecimal value) {
        this.value = value;
    }

}
