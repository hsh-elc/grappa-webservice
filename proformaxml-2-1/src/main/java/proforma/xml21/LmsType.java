
package proforma.xml21;

import org.w3c.dom.Element;

import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for lms-type complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="lms-type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="submission-datetime" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="user-id" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="course-id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;any processContents='lax' namespace='##other' maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="url" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "lms-type", namespace = "urn:proforma:v2.1", propOrder = {
    "submissionDatetime",
    "userId",
    "courseId",
    "any"
})
public class LmsType {

    @XmlElement(name = "submission-datetime", namespace = "urn:proforma:v2.1", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar submissionDatetime;
    @XmlElement(name = "user-id", namespace = "urn:proforma:v2.1")
    protected List<String> userId;
    @XmlElement(name = "course-id", namespace = "urn:proforma:v2.1")
    protected String courseId;
    @XmlAnyElement(lax = true)
    protected List<Object> any;
    @XmlAttribute(name = "url")
    protected String url;

    /**
     * Gets the value of the submissionDatetime property.
     *
     * @return possible object is
     * {@link XMLGregorianCalendar }
     */
    public XMLGregorianCalendar getSubmissionDatetime() {
        return submissionDatetime;
    }

    /**
     * Sets the value of the submissionDatetime property.
     *
     * @param value allowed object is
     *              {@link XMLGregorianCalendar }
     */
    public void setSubmissionDatetime(XMLGregorianCalendar value) {
        this.submissionDatetime = value;
    }

    /**
     * Gets the value of the userId property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the userId property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getUserId().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     */
    public List<String> getUserId() {
        if (userId == null) {
            userId = new ArrayList<String>();
        }
        return this.userId;
    }

    /**
     * Gets the value of the courseId property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getCourseId() {
        return courseId;
    }

    /**
     * Sets the value of the courseId property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setCourseId(String value) {
        this.courseId = value;
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

    /**
     * Gets the value of the url property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets the value of the url property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setUrl(String value) {
        this.url = value;
    }

}
