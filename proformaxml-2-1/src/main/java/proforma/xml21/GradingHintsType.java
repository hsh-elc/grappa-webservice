
package proforma.xml21;

import org.w3c.dom.Element;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


/**
 * Root element type of a ProFormA grading-hints element. This includes the complete
 * hierarchical grading scheme with all tests references, weights, accumulating functions and nullify conditions.
 * Hierarchy nodes and conditions can get a title and descriptions. All information below this element except the
 * root node is optional. Grader-specific hints from other XML namespaces can be included in xs:any elements.
 *
 *
 * <p>Java class for grading-hints-type complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="grading-hints-type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="root" type="{urn:proforma:v2.1}grades-node-type"/>
 *         &lt;element name="combine" type="{urn:proforma:v2.1}grades-node-type" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;any processContents='lax' namespace='##other' maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "grading-hints-type", namespace = "urn:proforma:v2.1", propOrder = {
    "root",
    "combine",
    "any"
})
public class GradingHintsType {

    @XmlElement(namespace = "urn:proforma:v2.1", required = true)
    protected GradesNodeType root;
    @XmlElement(namespace = "urn:proforma:v2.1")
    protected List<GradesNodeType> combine;
    @XmlAnyElement(lax = true)
    protected List<Object> any;

    /**
     * Gets the value of the root property.
     *
     * @return possible object is
     * {@link GradesNodeType }
     */
    public GradesNodeType getRoot() {
        return root;
    }

    /**
     * Sets the value of the root property.
     *
     * @param value allowed object is
     *              {@link GradesNodeType }
     */
    public void setRoot(GradesNodeType value) {
        this.root = value;
    }

    /**
     * Gets the value of the combine property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the combine property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCombine().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link GradesNodeType }
     */
    public List<GradesNodeType> getCombine() {
        if (combine == null) {
            combine = new ArrayList<GradesNodeType>();
        }
        return this.combine;
    }

    /**
     * Gets the value of the any property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the any property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAny().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * {@link Element }
     */
    public List<Object> getAny() {
        if (any == null) {
            any = new ArrayList<Object>();
        }
        return this.any;
    }

}
