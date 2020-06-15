
package de.hsh.grappa.proformaxml.v201;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java-Klasse f�r result-spec-type complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="result-spec-type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="student-feedback-level" type="{urn:proforma:v2.0.1}feedback-level-type" minOccurs="0"/>
 *         &lt;element name="teacher-feedback-level" type="{urn:proforma:v2.0.1}feedback-level-type" minOccurs="0"/>
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
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "result-spec-type", namespace = "urn:proforma:v2.0.1", propOrder = {
    "studentFeedbackLevel",
    "teacherFeedbackLevel"
})
public class ResultSpecType {

    @XmlElement(name = "student-feedback-level", namespace = "urn:proforma:v2.0.1")
    @XmlSchemaType(name = "string")
    protected FeedbackLevelType studentFeedbackLevel;
    @XmlElement(name = "teacher-feedback-level", namespace = "urn:proforma:v2.0.1")
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
     * Ruft den Wert der studentFeedbackLevel-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link FeedbackLevelType }
     *     
     */
    public FeedbackLevelType getStudentFeedbackLevel() {
        return studentFeedbackLevel;
    }

    /**
     * Legt den Wert der studentFeedbackLevel-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link FeedbackLevelType }
     *     
     */
    public void setStudentFeedbackLevel(FeedbackLevelType value) {
        this.studentFeedbackLevel = value;
    }

    /**
     * Ruft den Wert der teacherFeedbackLevel-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link FeedbackLevelType }
     *     
     */
    public FeedbackLevelType getTeacherFeedbackLevel() {
        return teacherFeedbackLevel;
    }

    /**
     * Legt den Wert der teacherFeedbackLevel-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link FeedbackLevelType }
     *     
     */
    public void setTeacherFeedbackLevel(FeedbackLevelType value) {
        this.teacherFeedbackLevel = value;
    }

    /**
     * Ruft den Wert der format-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFormat() {
        return format;
    }

    /**
     * Legt den Wert der format-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFormat(String value) {
        this.format = value;
    }

    /**
     * Ruft den Wert der structure-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStructure() {
        return structure;
    }

    /**
     * Legt den Wert der structure-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStructure(String value) {
        this.structure = value;
    }

    /**
     * Ruft den Wert der lang-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLang() {
        return lang;
    }

    /**
     * Legt den Wert der lang-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLang(String value) {
        this.lang = value;
    }

}
