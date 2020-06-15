
package de.hsh.grappa.proformaxml.v300;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für merged-test-feedback-type complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="merged-test-feedback-type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="overall-result" type="{urn:proforma:v3.0.0}overall-result-type"/>
 *         &lt;element name="student-feedback" type="{urn:proforma:v3.0.0}merged-feedback-type" minOccurs="0"/>
 *         &lt;element name="teacher-feedback" type="{urn:proforma:v3.0.0}merged-feedback-type" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "merged-test-feedback-type", namespace = "urn:proforma:v3.0.0", propOrder = {
    "overallResult",
    "studentFeedback",
    "teacherFeedback"
})
public class MergedTestFeedbackType {

    @XmlElement(name = "overall-result", namespace = "urn:proforma:v3.0.0", required = true)
    protected OverallResultType overallResult;
    @XmlElement(name = "student-feedback", namespace = "urn:proforma:v3.0.0")
    protected String studentFeedback;
    @XmlElement(name = "teacher-feedback", namespace = "urn:proforma:v3.0.0")
    protected String teacherFeedback;

    /**
     * Ruft den Wert der overallResult-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link OverallResultType }
     *     
     */
    public OverallResultType getOverallResult() {
        return overallResult;
    }

    /**
     * Legt den Wert der overallResult-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link OverallResultType }
     *     
     */
    public void setOverallResult(OverallResultType value) {
        this.overallResult = value;
    }

    /**
     * Ruft den Wert der studentFeedback-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStudentFeedback() {
        return studentFeedback;
    }

    /**
     * Legt den Wert der studentFeedback-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStudentFeedback(String value) {
        this.studentFeedback = value;
    }

    /**
     * Ruft den Wert der teacherFeedback-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTeacherFeedback() {
        return teacherFeedback;
    }

    /**
     * Legt den Wert der teacherFeedback-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTeacherFeedback(String value) {
        this.teacherFeedback = value;
    }

}
