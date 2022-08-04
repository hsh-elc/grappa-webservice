
package proforma.xml21;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for test-result-type complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="test-result-type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="result" type="{urn:proforma:v2.1}result-type"/>
 *         &lt;element name="feedback-list" type="{urn:proforma:v2.1}feedback-list-type"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "test-result-type", namespace = "urn:proforma:v2.1", propOrder = {
    "result",
    "feedbackList"
})
public class TestResultType {

    @XmlElement(namespace = "urn:proforma:v2.1", required = true)
    protected ResultType result;
    @XmlElement(name = "feedback-list", namespace = "urn:proforma:v2.1", required = true)
    protected FeedbackListType feedbackList;

    /**
     * Gets the value of the result property.
     *
     * @return possible object is
     * {@link ResultType }
     */
    public ResultType getResult() {
        return result;
    }

    /**
     * Sets the value of the result property.
     *
     * @param value allowed object is
     *              {@link ResultType }
     */
    public void setResult(ResultType value) {
        this.result = value;
    }

    /**
     * Gets the value of the feedbackList property.
     *
     * @return possible object is
     * {@link FeedbackListType }
     */
    public FeedbackListType getFeedbackList() {
        return feedbackList;
    }

    /**
     * Sets the value of the feedbackList property.
     *
     * @param value allowed object is
     *              {@link FeedbackListType }
     */
    public void setFeedbackList(FeedbackListType value) {
        this.feedbackList = value;
    }

}
