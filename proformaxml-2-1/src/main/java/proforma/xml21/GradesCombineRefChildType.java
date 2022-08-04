
package proforma.xml21;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * A "combine-ref" node points to a "combine" node in the grading scheme hierarchy.
 * As such the result of the pointed at node is obtained and included in a bottom-up fashion in the calculation of
 * the total result.
 *
 *
 * <p>Java class for grades-combine-ref-child-type complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="grades-combine-ref-child-type">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:proforma:v2.1}grades-base-ref-child-type">
 *       &lt;attribute name="ref" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "grades-combine-ref-child-type", namespace = "urn:proforma:v2.1")
public class GradesCombineRefChildType
    extends GradesBaseRefChildType {

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
