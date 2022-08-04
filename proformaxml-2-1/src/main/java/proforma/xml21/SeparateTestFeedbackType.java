
package proforma.xml21;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for separate-test-feedback-type complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="separate-test-feedback-type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="submission-feedback-list" type="{urn:proforma:v2.1}feedback-list-type"/>
 *         &lt;element name="tests-response" type="{urn:proforma:v2.1}tests-response-type"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "separate-test-feedback-type", namespace = "urn:proforma:v2.1", propOrder = {
    "submissionFeedbackList",
    "testsResponse"
})
public class SeparateTestFeedbackType {

    @XmlElement(name = "submission-feedback-list", namespace = "urn:proforma:v2.1", required = true)
    protected FeedbackListType submissionFeedbackList;
    @XmlElement(name = "tests-response", namespace = "urn:proforma:v2.1", required = true)
    protected TestsResponseType testsResponse;

    /**
     * Gets the value of the submissionFeedbackList property.
     *
     * @return possible object is
     * {@link FeedbackListType }
     */
    public FeedbackListType getSubmissionFeedbackList() {
        return submissionFeedbackList;
    }

    /**
     * Sets the value of the submissionFeedbackList property.
     *
     * @param value allowed object is
     *              {@link FeedbackListType }
     */
    public void setSubmissionFeedbackList(FeedbackListType value) {
        this.submissionFeedbackList = value;
    }

    /**
     * Gets the value of the testsResponse property.
     *
     * @return possible object is
     * {@link TestsResponseType }
     */
    public TestsResponseType getTestsResponse() {
        return testsResponse;
    }

    /**
     * Sets the value of the testsResponse property.
     *
     * @param value allowed object is
     *              {@link TestsResponseType }
     */
    public void setTestsResponse(TestsResponseType value) {
        this.testsResponse = value;
    }

}
