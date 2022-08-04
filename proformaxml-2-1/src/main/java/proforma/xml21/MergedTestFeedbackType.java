
package proforma.xml21;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for merged-test-feedback-type complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="merged-test-feedback-type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="overall-result" type="{urn:proforma:v2.1}overall-result-type"/>
 *         &lt;element name="student-feedback" type="{urn:proforma:v2.1}merged-feedback-type" minOccurs="0"/>
 *         &lt;element name="teacher-feedback" type="{urn:proforma:v2.1}merged-feedback-type" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "merged-test-feedback-type", namespace = "urn:proforma:v2.1", propOrder = {
    "overallResult",
    "studentFeedback",
    "teacherFeedback"
})
public class MergedTestFeedbackType {

    @XmlElement(name = "overall-result", namespace = "urn:proforma:v2.1", required = true)
    protected OverallResultType overallResult;
    @XmlElement(name = "student-feedback", namespace = "urn:proforma:v2.1")
    protected String studentFeedback;
    @XmlElement(name = "teacher-feedback", namespace = "urn:proforma:v2.1")
    protected String teacherFeedback;

    /**
     * Gets the value of the overallResult property.
     *
     * @return possible object is
     * {@link OverallResultType }
     */
    public OverallResultType getOverallResult() {
        return overallResult;
    }

    /**
     * Sets the value of the overallResult property.
     *
     * @param value allowed object is
     *              {@link OverallResultType }
     */
    public void setOverallResult(OverallResultType value) {
        this.overallResult = value;
    }

    /**
     * Gets the value of the studentFeedback property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getStudentFeedback() {
        return studentFeedback;
    }

    /**
     * Sets the value of the studentFeedback property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setStudentFeedback(String value) {
        this.studentFeedback = value;
    }

    /**
     * Gets the value of the teacherFeedback property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getTeacherFeedback() {
        return teacherFeedback;
    }

    /**
     * Sets the value of the teacherFeedback property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setTeacherFeedback(String value) {
        this.teacherFeedback = value;
    }

}
