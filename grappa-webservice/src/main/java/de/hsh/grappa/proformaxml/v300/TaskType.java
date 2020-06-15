
package de.hsh.grappa.proformaxml.v300;

import de.hsh.grappa.proforma.AbstractTaskType;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java-Klasse f�r task-type complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="task-type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="title" type="{urn:proforma:v3.0.0}title-type"/>
 *         &lt;element name="description" type="{urn:proforma:v3.0.0}description-type"/>
 *         &lt;element name="internal-description" type="{urn:proforma:v3.0.0}description-type" minOccurs="0"/>
 *         &lt;element name="proglang" type="{urn:proforma:v3.0.0}proglang-type"/>
 *         &lt;element name="submission-restrictions" type="{urn:proforma:v3.0.0}submission-restrictions-type" minOccurs="0"/>
 *         &lt;element name="files" type="{urn:proforma:v3.0.0}task-files-type"/>
 *         &lt;element name="external-resources" type="{urn:proforma:v3.0.0}external-resources-type" minOccurs="0"/>
 *         &lt;element name="model-solutions" type="{urn:proforma:v3.0.0}model-solutions-type"/>
 *         &lt;element name="tests" type="{urn:proforma:v3.0.0}tests-type"/>
 *         &lt;element name="grading-hints" type="{urn:proforma:v3.0.0}grading-hints-type" minOccurs="0"/>
 *         &lt;element name="meta-data" type="{urn:proforma:v3.0.0}task-meta-data-type"/>
 *       &lt;/sequence>
 *       &lt;attribute name="uuid" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="parent-uuid" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="lang" type="{http://www.w3.org/2001/XMLSchema}language" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "task-type", namespace = "urn:proforma:v3.0.0", propOrder = {
    "title",
    "description",
    "internalDescription",
    "proglang",
    "submissionRestrictions",
    "files",
    "externalResources",
    "modelSolutions",
    "tests",
    "gradingHints",
    "metaData"
})
public class TaskType implements AbstractTaskType {

    @XmlElement(namespace = "urn:proforma:v3.0.0", required = true)
    protected String title;
    @XmlElement(namespace = "urn:proforma:v3.0.0", required = true)
    protected String description;
    @XmlElement(name = "internal-description", namespace = "urn:proforma:v3.0.0")
    protected String internalDescription;
    @XmlElement(namespace = "urn:proforma:v3.0.0", required = true)
    protected ProglangType proglang;
    @XmlElement(name = "submission-restrictions", namespace = "urn:proforma:v3.0.0")
    protected SubmissionRestrictionsType submissionRestrictions;
    @XmlElement(namespace = "urn:proforma:v3.0.0", required = true)
    protected TaskFilesType files;
    @XmlElement(name = "external-resources", namespace = "urn:proforma:v3.0.0")
    protected ExternalResourcesType externalResources;
    @XmlElement(name = "model-solutions", namespace = "urn:proforma:v3.0.0", required = true)
    protected ModelSolutionsType modelSolutions;
    @XmlElement(namespace = "urn:proforma:v3.0.0", required = true)
    protected TestsType tests;
    @XmlElement(name = "grading-hints", namespace = "urn:proforma:v3.0.0")
    protected GradingHintsType gradingHints;
    @XmlElement(name = "meta-data", namespace = "urn:proforma:v3.0.0", required = true)
    protected TaskMetaDataType metaData;
    @XmlAttribute(name = "uuid", required = true)
    protected String uuid;
    @XmlAttribute(name = "parent-uuid")
    protected String parentUuid;
    @XmlAttribute(name = "lang")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "language")
    protected String lang;

    /**
     * Ruft den Wert der title-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTitle() {
        return title;
    }

    /**
     * Legt den Wert der title-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTitle(String value) {
        this.title = value;
    }

    /**
     * Ruft den Wert der description-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Legt den Wert der description-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Ruft den Wert der internalDescription-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInternalDescription() {
        return internalDescription;
    }

    /**
     * Legt den Wert der internalDescription-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInternalDescription(String value) {
        this.internalDescription = value;
    }

    /**
     * Ruft den Wert der proglang-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ProglangType }
     *     
     */
    public ProglangType getProglang() {
        return proglang;
    }

    /**
     * Legt den Wert der proglang-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ProglangType }
     *     
     */
    public void setProglang(ProglangType value) {
        this.proglang = value;
    }

    /**
     * Ruft den Wert der submissionRestrictions-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link SubmissionRestrictionsType }
     *     
     */
    public SubmissionRestrictionsType getSubmissionRestrictions() {
        return submissionRestrictions;
    }

    /**
     * Legt den Wert der submissionRestrictions-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link SubmissionRestrictionsType }
     *     
     */
    public void setSubmissionRestrictions(SubmissionRestrictionsType value) {
        this.submissionRestrictions = value;
    }

    /**
     * Ruft den Wert der files-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link TaskFilesType }
     *     
     */
    public TaskFilesType getFiles() {
        return files;
    }

    /**
     * Legt den Wert der files-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link TaskFilesType }
     *     
     */
    public void setFiles(TaskFilesType value) {
        this.files = value;
    }

    /**
     * Ruft den Wert der externalResources-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ExternalResourcesType }
     *     
     */
    public ExternalResourcesType getExternalResources() {
        return externalResources;
    }

    /**
     * Legt den Wert der externalResources-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ExternalResourcesType }
     *     
     */
    public void setExternalResources(ExternalResourcesType value) {
        this.externalResources = value;
    }

    /**
     * Ruft den Wert der modelSolutions-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ModelSolutionsType }
     *     
     */
    public ModelSolutionsType getModelSolutions() {
        return modelSolutions;
    }

    /**
     * Legt den Wert der modelSolutions-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ModelSolutionsType }
     *     
     */
    public void setModelSolutions(ModelSolutionsType value) {
        this.modelSolutions = value;
    }

    /**
     * Ruft den Wert der tests-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link TestsType }
     *     
     */
    public TestsType getTests() {
        return tests;
    }

    /**
     * Legt den Wert der tests-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link TestsType }
     *     
     */
    public void setTests(TestsType value) {
        this.tests = value;
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
     * Ruft den Wert der metaData-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link TaskMetaDataType }
     *     
     */
    public TaskMetaDataType getMetaData() {
        return metaData;
    }

    /**
     * Legt den Wert der metaData-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link TaskMetaDataType }
     *     
     */
    public void setMetaData(TaskMetaDataType value) {
        this.metaData = value;
    }

    /**
     * Ruft den Wert der uuid-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Legt den Wert der uuid-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUuid(String value) {
        this.uuid = value;
    }

    /**
     * Ruft den Wert der parentUuid-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getParentUuid() {
        return parentUuid;
    }

    /**
     * Legt den Wert der parentUuid-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setParentUuid(String value) {
        this.parentUuid = value;
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
