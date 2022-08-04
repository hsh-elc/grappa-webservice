
package proforma.xml21;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for result-spec-type complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="result-spec-type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="student-feedback-level" type="{urn:proforma:v2.1}feedback-level-type" minOccurs="0"/>
 *         &lt;element name="teacher-feedback-level" type="{urn:proforma:v2.1}feedback-level-type" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="format" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="xml"/>
 *             &lt;enumeration value="zip"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="structure" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="merged-test-feedback"/>
 *             &lt;enumeration value="separate-test-feedback"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="lang" type="{http://www.w3.org/2001/XMLSchema}language" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "result-spec-type", namespace = "urn:proforma:v2.1", propOrder = {
    "studentFeedbackLevel",
    "teacherFeedbackLevel"
})
public class ResultSpecType {

    @XmlElement(name = "student-feedback-level", namespace = "urn:proforma:v2.1")
    @XmlSchemaType(name = "string")
    protected FeedbackLevelType studentFeedbackLevel;
    @XmlElement(name = "teacher-feedback-level", namespace = "urn:proforma:v2.1")
    @XmlSchemaType(name = "string")
    protected FeedbackLevelType teacherFeedbackLevel;
    @XmlAttribute(name = "format", required = true)
    protected String format;
    @XmlAttribute(name = "structure", required = true)
    protected String structure;
    @XmlAttribute(name = "lang")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "language")
    protected String lang;

    /**
     * Gets the value of the studentFeedbackLevel property.
     *
     * @return possible object is
     * {@link FeedbackLevelType }
     */
    public FeedbackLevelType getStudentFeedbackLevel() {
        return studentFeedbackLevel;
    }

    /**
     * Sets the value of the studentFeedbackLevel property.
     *
     * @param value allowed object is
     *              {@link FeedbackLevelType }
     */
    public void setStudentFeedbackLevel(FeedbackLevelType value) {
        this.studentFeedbackLevel = value;
    }

    /**
     * Gets the value of the teacherFeedbackLevel property.
     *
     * @return possible object is
     * {@link FeedbackLevelType }
     */
    public FeedbackLevelType getTeacherFeedbackLevel() {
        return teacherFeedbackLevel;
    }

    /**
     * Sets the value of the teacherFeedbackLevel property.
     *
     * @param value allowed object is
     *              {@link FeedbackLevelType }
     */
    public void setTeacherFeedbackLevel(FeedbackLevelType value) {
        this.teacherFeedbackLevel = value;
    }

    /**
     * Gets the value of the format property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getFormat() {
        return format;
    }

    /**
     * Sets the value of the format property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setFormat(String value) {
        this.format = value;
    }

    /**
     * Gets the value of the structure property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getStructure() {
        return structure;
    }

    /**
     * Sets the value of the structure property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setStructure(String value) {
        this.structure = value;
    }

    /**
     * Gets the value of the lang property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getLang() {
        return lang;
    }

    /**
     * Sets the value of the lang property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setLang(String value) {
        this.lang = value;
    }

}
