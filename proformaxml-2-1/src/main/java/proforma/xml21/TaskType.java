
package proforma.xml21;

import proforma.xml.AbstractTaskType;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for task-type complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="task-type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="title" type="{urn:proforma:v2.1}title-type"/>
 *         &lt;element name="description" type="{urn:proforma:v2.1}description-type"/>
 *         &lt;element name="internal-description" type="{urn:proforma:v2.1}description-type" minOccurs="0"/>
 *         &lt;element name="proglang" type="{urn:proforma:v2.1}proglang-type"/>
 *         &lt;element name="submission-restrictions" type="{urn:proforma:v2.1}submission-restrictions-type" minOccurs="0"/>
 *         &lt;element name="files" type="{urn:proforma:v2.1}task-files-type"/>
 *         &lt;element name="external-resources" type="{urn:proforma:v2.1}external-resources-type" minOccurs="0"/>
 *         &lt;element name="model-solutions" type="{urn:proforma:v2.1}model-solutions-type" minOccurs="0"/>
 *         &lt;element name="tests" type="{urn:proforma:v2.1}tests-type"/>
 *         &lt;element name="grading-hints" type="{urn:proforma:v2.1}grading-hints-type" minOccurs="0"/>
 *         &lt;element name="meta-data" type="{urn:proforma:v2.1}task-meta-data-type"/>
 *       &lt;/sequence>
 *       &lt;attribute name="uuid" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="parent-uuid" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="lang" type="{http://www.w3.org/2001/XMLSchema}language" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */

@XmlRootElement(name = "task")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "task-type", namespace = "urn:proforma:v2.1", propOrder = {
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

//	@Override
//	public Class<? extends AbstractProformaType> getContextClass() {
//		return TaskType.class;
//	}

    @XmlElement(namespace = "urn:proforma:v2.1", required = true)
    protected String title;
    @XmlElement(namespace = "urn:proforma:v2.1", required = true)
    protected String description;
    @XmlElement(name = "internal-description", namespace = "urn:proforma:v2.1")
    protected String internalDescription;
    @XmlElement(namespace = "urn:proforma:v2.1", required = true)
    protected ProglangType proglang;
    @XmlElement(name = "submission-restrictions", namespace = "urn:proforma:v2.1")
    protected SubmissionRestrictionsType submissionRestrictions;
    @XmlElement(namespace = "urn:proforma:v2.1", required = true)
    protected TaskFilesType files;
    @XmlElement(name = "external-resources", namespace = "urn:proforma:v2.1")
    protected ExternalResourcesType externalResources;
    @XmlElement(name = "model-solutions", namespace = "urn:proforma:v2.1")
    protected ModelSolutionsType modelSolutions;
    @XmlElement(namespace = "urn:proforma:v2.1", required = true)
    protected TestsType tests;
    @XmlElement(name = "grading-hints", namespace = "urn:proforma:v2.1")
    protected GradingHintsType gradingHints;
    @XmlElement(name = "meta-data", namespace = "urn:proforma:v2.1", required = true)
    protected TaskMetaDataType metaData;
    @XmlAttribute(name = "uuid", required = true)
    protected String uuid;
    @XmlAttribute(name = "parent-uuid")
    protected String parentUuid;
    @XmlAttribute(name = "lang")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "language")
    protected String lang;

    @Override
    public String proFormAVersionNumber() {
        return "2.1";
    }

    /**
     * Gets the value of the title property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the value of the title property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setTitle(String value) {
        this.title = value;
    }

    /**
     * Gets the value of the description property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the internalDescription property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getInternalDescription() {
        return internalDescription;
    }

    /**
     * Sets the value of the internalDescription property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setInternalDescription(String value) {
        this.internalDescription = value;
    }

    /**
     * Gets the value of the proglang property.
     *
     * @return possible object is
     * {@link ProglangType }
     */
    public ProglangType getProglang() {
        return proglang;
    }

    /**
     * Sets the value of the proglang property.
     *
     * @param value allowed object is
     *              {@link ProglangType }
     */
    public void setProglang(ProglangType value) {
        this.proglang = value;
    }

    /**
     * Gets the value of the submissionRestrictions property.
     *
     * @return possible object is
     * {@link SubmissionRestrictionsType }
     */
    public SubmissionRestrictionsType getSubmissionRestrictions() {
        return submissionRestrictions;
    }

    /**
     * Sets the value of the submissionRestrictions property.
     *
     * @param value allowed object is
     *              {@link SubmissionRestrictionsType }
     */
    public void setSubmissionRestrictions(SubmissionRestrictionsType value) {
        this.submissionRestrictions = value;
    }

    /**
     * Gets the value of the files property.
     *
     * @return possible object is
     * {@link TaskFilesType }
     */
    public TaskFilesType getFiles() {
        return files;
    }

    /**
     * Sets the value of the files property.
     *
     * @param value allowed object is
     *              {@link TaskFilesType }
     */
    public void setFiles(TaskFilesType value) {
        this.files = value;
    }

    /**
     * Gets the value of the externalResources property.
     *
     * @return possible object is
     * {@link ExternalResourcesType }
     */
    public ExternalResourcesType getExternalResources() {
        return externalResources;
    }

    /**
     * Sets the value of the externalResources property.
     *
     * @param value allowed object is
     *              {@link ExternalResourcesType }
     */
    public void setExternalResources(ExternalResourcesType value) {
        this.externalResources = value;
    }

    /**
     * Gets the value of the modelSolutions property.
     *
     * @return possible object is
     * {@link ModelSolutionsType }
     */
    public ModelSolutionsType getModelSolutions() {
        return modelSolutions;
    }

    /**
     * Sets the value of the modelSolutions property.
     *
     * @param value allowed object is
     *              {@link ModelSolutionsType }
     */
    public void setModelSolutions(ModelSolutionsType value) {
        this.modelSolutions = value;
    }

    /**
     * Gets the value of the tests property.
     *
     * @return possible object is
     * {@link TestsType }
     */
    public TestsType getTests() {
        return tests;
    }

    /**
     * Sets the value of the tests property.
     *
     * @param value allowed object is
     *              {@link TestsType }
     */
    public void setTests(TestsType value) {
        this.tests = value;
    }

    /**
     * Gets the value of the gradingHints property.
     *
     * @return possible object is
     * {@link GradingHintsType }
     */
    public GradingHintsType getGradingHints() {
        return gradingHints;
    }

    /**
     * Sets the value of the gradingHints property.
     *
     * @param value allowed object is
     *              {@link GradingHintsType }
     */
    public void setGradingHints(GradingHintsType value) {
        this.gradingHints = value;
    }

    /**
     * Gets the value of the metaData property.
     *
     * @return possible object is
     * {@link TaskMetaDataType }
     */
    public TaskMetaDataType getMetaData() {
        return metaData;
    }

    /**
     * Sets the value of the metaData property.
     *
     * @param value allowed object is
     *              {@link TaskMetaDataType }
     */
    public void setMetaData(TaskMetaDataType value) {
        this.metaData = value;
    }

    /**
     * Gets the value of the uuid property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Sets the value of the uuid property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setUuid(String value) {
        this.uuid = value;
    }

    /**
     * Gets the value of the parentUuid property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getParentUuid() {
        return parentUuid;
    }

    /**
     * Sets the value of the parentUuid property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setParentUuid(String value) {
        this.parentUuid = value;
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
