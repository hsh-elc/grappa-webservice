
package proforma.xml21;

import proforma.xml.AbstractResponseType;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for response-type complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="response-type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;element name="merged-test-feedback" type="{urn:proforma:v2.1}merged-test-feedback-type"/>
 *           &lt;element name="separate-test-feedback" type="{urn:proforma:v2.1}separate-test-feedback-type"/>
 *         &lt;/choice>
 *         &lt;element name="files" type="{urn:proforma:v2.1}response-files-type"/>
 *         &lt;element name="response-meta-data" type="{urn:proforma:v2.1}response-meta-data-type"/>
 *       &lt;/sequence>
 *       &lt;attribute name="lang" type="{http://www.w3.org/2001/XMLSchema}language" />
 *       &lt;attribute name="submission-id" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */

@XmlRootElement(name = "response")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "response-type", namespace = "urn:proforma:v2.1", propOrder = {
    "mergedTestFeedback",
    "separateTestFeedback",
    "files",
    "responseMetaData"
})
public class ResponseType implements AbstractResponseType {

//	@Override
//	public Class<? extends AbstractProformaType> getContextClass() {
//		return ResponseType.class;
//	}

    @XmlElement(name = "merged-test-feedback", namespace = "urn:proforma:v2.1")
    protected MergedTestFeedbackType mergedTestFeedback;
    @XmlElement(name = "separate-test-feedback", namespace = "urn:proforma:v2.1")
    protected SeparateTestFeedbackType separateTestFeedback;
    @XmlElement(namespace = "urn:proforma:v2.1", required = true)
    protected ResponseFilesType files;
    @XmlElement(name = "response-meta-data", namespace = "urn:proforma:v2.1", required = true)
    protected ResponseMetaDataType responseMetaData;
    @XmlAttribute(name = "lang")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "language")
    protected String lang;
    @XmlAttribute(name = "submission-id")
    protected String submissionId;


    @Override
    public String proFormAVersionNumber() {
        return "2.1";
    }


    /**
     * Gets the value of the mergedTestFeedback property.
     *
     * @return possible object is
     * {@link MergedTestFeedbackType }
     */
    public MergedTestFeedbackType getMergedTestFeedback() {
        return mergedTestFeedback;
    }

    /**
     * Sets the value of the mergedTestFeedback property.
     *
     * @param value allowed object is
     *              {@link MergedTestFeedbackType }
     */
    public void setMergedTestFeedback(MergedTestFeedbackType value) {
        this.mergedTestFeedback = value;
    }

    /**
     * Gets the value of the separateTestFeedback property.
     *
     * @return possible object is
     * {@link SeparateTestFeedbackType }
     */
    public SeparateTestFeedbackType getSeparateTestFeedback() {
        return separateTestFeedback;
    }

    /**
     * Sets the value of the separateTestFeedback property.
     *
     * @param value allowed object is
     *              {@link SeparateTestFeedbackType }
     */
    public void setSeparateTestFeedback(SeparateTestFeedbackType value) {
        this.separateTestFeedback = value;
    }

    /**
     * Gets the value of the files property.
     *
     * @return possible object is
     * {@link ResponseFilesType }
     */
    public ResponseFilesType getFiles() {
        return files;
    }

    /**
     * Sets the value of the files property.
     *
     * @param value allowed object is
     *              {@link ResponseFilesType }
     */
    public void setFiles(ResponseFilesType value) {
        this.files = value;
    }

    /**
     * Gets the value of the responseMetaData property.
     *
     * @return possible object is
     * {@link ResponseMetaDataType }
     */
    public ResponseMetaDataType getResponseMetaData() {
        return responseMetaData;
    }

    /**
     * Sets the value of the responseMetaData property.
     *
     * @param value allowed object is
     *              {@link ResponseMetaDataType }
     */
    public void setResponseMetaData(ResponseMetaDataType value) {
        this.responseMetaData = value;
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

    /**
     * Gets the value of the submissionId property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getSubmissionId() {
        return submissionId;
    }

    /**
     * Sets the value of the submissionId property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setSubmissionId(String value) {
        this.submissionId = value;
    }

}
