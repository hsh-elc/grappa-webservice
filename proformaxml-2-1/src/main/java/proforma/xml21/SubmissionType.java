
package proforma.xml21;

import proforma.xml.AbstractSubmissionType;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for submission-type complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="submission-type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;element name="external-task" type="{urn:proforma:v2.1}external-task-type"/>
 *           &lt;element name="included-task-file" type="{urn:proforma:v2.1}included-task-file-type"/>
 *           &lt;element name="task" type="{urn:proforma:v2.1}task-type"/>
 *         &lt;/choice>
 *         &lt;element name="grading-hints" type="{urn:proforma:v2.1}grading-hints-type" minOccurs="0"/>
 *         &lt;choice>
 *           &lt;element name="external-submission" type="{urn:proforma:v2.1}external-submission-type"/>
 *           &lt;element name="files" type="{urn:proforma:v2.1}submission-files-type"/>
 *         &lt;/choice>
 *         &lt;element name="lms" type="{urn:proforma:v2.1}lms-type" minOccurs="0"/>
 *         &lt;element name="result-spec" type="{urn:proforma:v2.1}result-spec-type"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */

@XmlRootElement(name = "submission")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "submission-type", namespace = "urn:proforma:v2.1", propOrder = {
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

//	@Override
//	public Class<? extends AbstractProformaType> getContextClass() {
//		return SubmissionType.class;
//	}

    @XmlElement(name = "external-task", namespace = "urn:proforma:v2.1")
    protected ExternalTaskType externalTask;
    @XmlElement(name = "included-task-file", namespace = "urn:proforma:v2.1")
    protected IncludedTaskFileType includedTaskFile;
    @XmlElement(namespace = "urn:proforma:v2.1")
    protected TaskType task;
    @XmlElement(name = "grading-hints", namespace = "urn:proforma:v2.1")
    protected GradingHintsType gradingHints;
    @XmlElement(name = "external-submission", namespace = "urn:proforma:v2.1")
    protected ExternalSubmissionType externalSubmission;
    @XmlElement(namespace = "urn:proforma:v2.1")
    protected SubmissionFilesType files;
    @XmlElement(namespace = "urn:proforma:v2.1")
    protected LmsType lms;
    @XmlElement(name = "result-spec", namespace = "urn:proforma:v2.1", required = true)
    protected ResultSpecType resultSpec;
    @XmlAttribute(name = "id")
    protected String id;

    @Override
    public String proFormAVersionNumber() {
        return "2.1";
    }


    /**
     * Gets the value of the externalTask property.
     *
     * @return possible object is
     * {@link ExternalTaskType }
     */
    public ExternalTaskType getExternalTask() {
        return externalTask;
    }

    /**
     * Sets the value of the externalTask property.
     *
     * @param value allowed object is
     *              {@link ExternalTaskType }
     */
    public void setExternalTask(ExternalTaskType value) {
        this.externalTask = value;
    }

    /**
     * Gets the value of the includedTaskFile property.
     *
     * @return possible object is
     * {@link IncludedTaskFileType }
     */
    public IncludedTaskFileType getIncludedTaskFile() {
        return includedTaskFile;
    }

    /**
     * Sets the value of the includedTaskFile property.
     *
     * @param value allowed object is
     *              {@link IncludedTaskFileType }
     */
    public void setIncludedTaskFile(IncludedTaskFileType value) {
        this.includedTaskFile = value;
    }

    /**
     * Gets the value of the task property.
     *
     * @return possible object is
     * {@link TaskType }
     */
    public TaskType getTask() {
        return task;
    }

    /**
     * Sets the value of the task property.
     *
     * @param value allowed object is
     *              {@link TaskType }
     */
    public void setTask(TaskType value) {
        this.task = value;
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
     * Gets the value of the externalSubmission property.
     *
     * @return possible object is
     * {@link ExternalSubmissionType }
     */
    public ExternalSubmissionType getExternalSubmission() {
        return externalSubmission;
    }

    /**
     * Sets the value of the externalSubmission property.
     *
     * @param value allowed object is
     *              {@link ExternalSubmissionType }
     */
    public void setExternalSubmission(ExternalSubmissionType value) {
        this.externalSubmission = value;
    }

    /**
     * Gets the value of the files property.
     *
     * @return possible object is
     * {@link SubmissionFilesType }
     */
    public SubmissionFilesType getFiles() {
        return files;
    }

    /**
     * Sets the value of the files property.
     *
     * @param value allowed object is
     *              {@link SubmissionFilesType }
     */
    public void setFiles(SubmissionFilesType value) {
        this.files = value;
    }

    /**
     * Gets the value of the lms property.
     *
     * @return possible object is
     * {@link LmsType }
     */
    public LmsType getLms() {
        return lms;
    }

    /**
     * Sets the value of the lms property.
     *
     * @param value allowed object is
     *              {@link LmsType }
     */
    public void setLms(LmsType value) {
        this.lms = value;
    }

    /**
     * Gets the value of the resultSpec property.
     *
     * @return possible object is
     * {@link ResultSpecType }
     */
    public ResultSpecType getResultSpec() {
        return resultSpec;
    }

    /**
     * Sets the value of the resultSpec property.
     *
     * @param value allowed object is
     *              {@link ResultSpecType }
     */
    public void setResultSpec(ResultSpecType value) {
        this.resultSpec = value;
    }

    /**
     * Gets the value of the id property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setId(String value) {
        this.id = value;
    }

}
