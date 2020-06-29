
package de.hsh.grappa.proformaxml.v201;

import de.hsh.grappa.proforma.AbstractSubmissionType;

import javax.xml.bind.annotation.*;


/**
 * <p>Java-Klasse f�r submission-type complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="submission-type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;element name="external-task" type="{urn:proforma:v2.0.1}external-task-type"/>
 *           &lt;element name="included-task-file" type="{urn:proforma:v2.0.1}included-task-file-type"/>
 *           &lt;element name="task" type="{urn:proforma:v2.0.1}task-type"/>
 *         &lt;/choice>
 *         &lt;element name="grading-hints" type="{urn:proforma:v2.0.1}grading-hints-type" minOccurs="0"/>
 *         &lt;choice>
 *           &lt;element name="external-submission" type="{urn:proforma:v2.0.1}external-submission-type"/>
 *           &lt;element name="files" type="{urn:proforma:v2.0.1}submission-files-type"/>
 *         &lt;/choice>
 *         &lt;element name="lms" type="{urn:proforma:v2.0.1}lms-type" minOccurs="0"/>
 *         &lt;element name="result-spec" type="{urn:proforma:v2.0.1}result-spec-type"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlRootElement(name = "submission")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "submission-type", namespace = "urn:proforma:v2.0.1", propOrder = {
    "externalTask",
    "includedTaskFile",
    "task",
    "gradingHints",
    "externalSubmission",
    "files",
    "lms",
    "resultSpec"
})
public class SubmissionType implements AbstractSubmissionType {

    @XmlElement(name = "external-task", namespace = "urn:proforma:v2.0.1")
    protected ExternalTaskType externalTask;
    @XmlElement(name = "included-task-file", namespace = "urn:proforma:v2.0.1")
    protected IncludedTaskFileType includedTaskFile;
    @XmlElement(namespace = "urn:proforma:v2.0.1")
    protected TaskType task;
    @XmlElement(name = "grading-hints", namespace = "urn:proforma:v2.0.1")
    protected GradingHintsType gradingHints;
    @XmlElement(name = "external-submission", namespace = "urn:proforma:v2.0.1")
    protected String externalSubmission;
    @XmlElement(namespace = "urn:proforma:v2.0.1")
    protected SubmissionFilesType files;
    @XmlElement(namespace = "urn:proforma:v2.0.1")
    protected LmsType lms;
    @XmlElement(name = "result-spec", namespace = "urn:proforma:v2.0.1", required = true)
    protected ResultSpecType resultSpec;

    /**
     * Ruft den Wert der externalTask-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ExternalTaskType }
     *     
     */
    public ExternalTaskType getExternalTask() {
        return externalTask;
    }

    /**
     * Legt den Wert der externalTask-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ExternalTaskType }
     *     
     */
    public void setExternalTask(ExternalTaskType value) {
        this.externalTask = value;
    }

    /**
     * Ruft den Wert der includedTaskFile-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link IncludedTaskFileType }
     *     
     */
    public IncludedTaskFileType getIncludedTaskFile() {
        return includedTaskFile;
    }

    /**
     * Legt den Wert der includedTaskFile-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link IncludedTaskFileType }
     *     
     */
    public void setIncludedTaskFile(IncludedTaskFileType value) {
        this.includedTaskFile = value;
    }

    /**
     * Ruft den Wert der task-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link TaskType }
     *     
     */
    public TaskType getTask() {
        return task;
    }

    /**
     * Legt den Wert der task-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link TaskType }
     *     
     */
    public void setTask(TaskType value) {
        this.task = value;
    }

    /**
     * Ruft den Wert der gradingHints-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link GradingHintsType }
     *     
     */
    public GradingHintsType getGradingHints() {
        return gradingHints;
    }

    /**
     * Legt den Wert der gradingHints-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link GradingHintsType }
     *     
     */
    public void setGradingHints(GradingHintsType value) {
        this.gradingHints = value;
    }

    /**
     * Ruft den Wert der externalSubmission-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExternalSubmission() {
        return externalSubmission;
    }

    /**
     * Legt den Wert der externalSubmission-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExternalSubmission(String value) {
        this.externalSubmission = value;
    }

    /**
     * Ruft den Wert der files-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link SubmissionFilesType }
     *     
     */
    public SubmissionFilesType getFiles() {
        return files;
    }

    /**
     * Legt den Wert der files-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link SubmissionFilesType }
     *     
     */
    public void setFiles(SubmissionFilesType value) {
        this.files = value;
    }

    /**
     * Ruft den Wert der lms-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link LmsType }
     *     
     */
    public LmsType getLms() {
        return lms;
    }

    /**
     * Legt den Wert der lms-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link LmsType }
     *     
     */
    public void setLms(LmsType value) {
        this.lms = value;
    }

    /**
     * Ruft den Wert der resultSpec-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ResultSpecType }
     *     
     */
    public ResultSpecType getResultSpec() {
        return resultSpec;
    }

    /**
     * Legt den Wert der resultSpec-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ResultSpecType }
     *     
     */
    public void setResultSpec(ResultSpecType value) {
        this.resultSpec = value;
    }

}
