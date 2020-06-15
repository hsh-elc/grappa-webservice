
package de.hsh.grappa.proformaxml.v300;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java-Klasse f�r response-type complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="response-type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;element name="merged-test-feedback" type="{urn:proforma:v3.0.0}merged-test-feedback-type"/>
 *           &lt;element name="separate-test-feedback" type="{urn:proforma:v3.0.0}separate-test-feedback-type"/>
 *         &lt;/choice>
 *         &lt;element name="files" type="{urn:proforma:v3.0.0}response-files-type"/>
 *         &lt;element name="response-meta-data" type="{urn:proforma:v3.0.0}response-meta-data-type"/>
 *       &lt;/sequence>
 *       &lt;attribute name="lang" type="{http://www.w3.org/2001/XMLSchema}language" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "response-type", namespace = "urn:proforma:v3.0.0", propOrder = {
    "mergedTestFeedback",
    "separateTestFeedback",
    "files",
    "responseMetaData"
})
public class ResponseType {

    @XmlElement(name = "merged-test-feedback", namespace = "urn:proforma:v3.0.0")
    protected MergedTestFeedbackType mergedTestFeedback;
    @XmlElement(name = "separate-test-feedback", namespace = "urn:proforma:v3.0.0")
    protected SeparateTestFeedbackType separateTestFeedback;
    @XmlElement(namespace = "urn:proforma:v3.0.0", required = true)
    protected ResponseFilesType files;
    @XmlElement(name = "response-meta-data", namespace = "urn:proforma:v3.0.0", required = true)
    protected ResponseMetaDataType responseMetaData;
    @XmlAttribute(name = "lang")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "language")
    protected String lang;

    /**
     * Ruft den Wert der mergedTestFeedback-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link MergedTestFeedbackType }
     *     
     */
    public MergedTestFeedbackType getMergedTestFeedback() {
        return mergedTestFeedback;
    }

    /**
     * Legt den Wert der mergedTestFeedback-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link MergedTestFeedbackType }
     *     
     */
    public void setMergedTestFeedback(MergedTestFeedbackType value) {
        this.mergedTestFeedback = value;
    }

    /**
     * Ruft den Wert der separateTestFeedback-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link SeparateTestFeedbackType }
     *     
     */
    public SeparateTestFeedbackType getSeparateTestFeedback() {
        return separateTestFeedback;
    }

    /**
     * Legt den Wert der separateTestFeedback-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link SeparateTestFeedbackType }
     *     
     */
    public void setSeparateTestFeedback(SeparateTestFeedbackType value) {
        this.separateTestFeedback = value;
    }

    /**
     * Ruft den Wert der files-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ResponseFilesType }
     *     
     */
    public ResponseFilesType getFiles() {
        return files;
    }

    /**
     * Legt den Wert der files-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ResponseFilesType }
     *     
     */
    public void setFiles(ResponseFilesType value) {
        this.files = value;
    }

    /**
     * Ruft den Wert der responseMetaData-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ResponseMetaDataType }
     *     
     */
    public ResponseMetaDataType getResponseMetaData() {
        return responseMetaData;
    }

    /**
     * Legt den Wert der responseMetaData-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ResponseMetaDataType }
     *     
     */
    public void setResponseMetaData(ResponseMetaDataType value) {
        this.responseMetaData = value;
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
