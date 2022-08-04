
package proforma.xml21;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * An operand of a comparison expression pointing to a "combine" node.
 *
 *
 * <p>Java class for grades-nullify-combine-ref-type complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="grades-nullify-combine-ref-type">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:proforma:v2.1}grades-nullify-comparison-operand-type">
 *       &lt;attribute name="ref" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "grades-nullify-combine-ref-type", namespace = "urn:proforma:v2.1")
public class GradesNullifyCombineRefType
    extends GradesNullifyComparisonOperandType {

    @XmlAttribute(name = "ref", required = true)
    protected String ref;

    /**
     * Gets the value of the ref property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getRef() {
        return ref;
    }

    /**
     * Sets the value of the ref property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setRef(String value) {
        this.ref = value;
    }

}
