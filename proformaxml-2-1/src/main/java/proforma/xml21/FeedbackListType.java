
package proforma.xml21;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for feedback-list-type complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="feedback-list-type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence maxOccurs="unbounded">
 *         &lt;element name="student-feedback" type="{urn:proforma:v2.1}feedback-type" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="teacher-feedback" type="{urn:proforma:v2.1}feedback-type" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "feedback-list-type", namespace = "urn:proforma:v2.1", propOrder = {
    "studentFeedback",
    "teacherFeedback"
})
public class FeedbackListType {

    @XmlElement(name = "student-feedback", namespace = "urn:proforma:v2.1")
    protected List<FeedbackType> studentFeedback;
    @XmlElement(name = "teacher-feedback", namespace = "urn:proforma:v2.1")
    protected List<FeedbackType> teacherFeedback;

    /**
     * Gets the value of the studentFeedback property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the studentFeedback property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getStudentFeedback().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FeedbackType }
     */
    public List<FeedbackType> getStudentFeedback() {
        if (studentFeedback == null) {
            studentFeedback = new ArrayList<FeedbackType>();
        }
        return this.studentFeedback;
    }

    /**
     * Gets the value of the teacherFeedback property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the teacherFeedback property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTeacherFeedback().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FeedbackType }
     */
    public List<FeedbackType> getTeacherFeedback() {
        if (teacherFeedback == null) {
            teacherFeedback = new ArrayList<FeedbackType>();
        }
        return this.teacherFeedback;
    }

}
