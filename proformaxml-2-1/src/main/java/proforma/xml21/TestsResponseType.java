
package proforma.xml21;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for tests-response-type complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="tests-response-type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="test-response" type="{urn:proforma:v2.1}test-response-type" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tests-response-type", namespace = "urn:proforma:v2.1", propOrder = {
    "testResponse"
})
public class TestsResponseType {

    @XmlElement(name = "test-response", namespace = "urn:proforma:v2.1")
    protected List<TestResponseType> testResponse;

    /**
     * Gets the value of the testResponse property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the testResponse property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTestResponse().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TestResponseType }
     */
    public List<TestResponseType> getTestResponse() {
        if (testResponse == null) {
            testResponse = new ArrayList<TestResponseType>();
        }
        return this.testResponse;
    }

}
