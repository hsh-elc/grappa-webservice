
package proforma.xml21;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for test-response-type complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="test-response-type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element name="test-result" type="{urn:proforma:v2.1}test-result-type"/>
 *         &lt;element name="subtests-response" type="{urn:proforma:v2.1}subtests-response-type"/>
 *       &lt;/choice>
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "test-response-type", namespace = "urn:proforma:v2.1", propOrder = {
    "testResult",
    "subtestsResponse"
})
public class TestResponseType {

    @XmlElement(name = "test-result", namespace = "urn:proforma:v2.1")
    protected TestResultType testResult;
    @XmlElement(name = "subtests-response", namespace = "urn:proforma:v2.1")
    protected SubtestsResponseType subtestsResponse;
    @XmlAttribute(name = "id", required = true)
    protected String id;

    /**
     * Gets the value of the testResult property.
     *
     * @return possible object is
     * {@link TestResultType }
     */
    public TestResultType getTestResult() {
        return testResult;
    }

    /**
     * Sets the value of the testResult property.
     *
     * @param value allowed object is
     *              {@link TestResultType }
     */
    public void setTestResult(TestResultType value) {
        this.testResult = value;
    }

    /**
     * Gets the value of the subtestsResponse property.
     *
     * @return possible object is
     * {@link SubtestsResponseType }
     */
    public SubtestsResponseType getSubtestsResponse() {
        return subtestsResponse;
    }

    /**
     * Sets the value of the subtestsResponse property.
     *
     * @param value allowed object is
     *              {@link SubtestsResponseType }
     */
    public void setSubtestsResponse(SubtestsResponseType value) {
        this.subtestsResponse = value;
    }

    /**
     * Gets the value of the id property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setId(String value) {
        this.id = value;
    }

}
