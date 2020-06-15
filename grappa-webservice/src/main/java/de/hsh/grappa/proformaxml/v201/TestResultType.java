
package de.hsh.grappa.proformaxml.v201;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse f�r test-result-type complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="test-result-type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="result" type="{urn:proforma:v2.0.1}result-type"/>
 *         &lt;element name="feedback-list" type="{urn:proforma:v2.0.1}feedback-list-type"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "test-result-type", namespace = "urn:proforma:v2.0.1", propOrder = {
    "result",
    "feedbackList"
})
public class TestResultType {

    @XmlElement(namespace = "urn:proforma:v2.0.1", required = true)
    protected ResultType result;
    @XmlElement(name = "feedback-list", namespace = "urn:proforma:v2.0.1", required = true)
    protected FeedbackListType feedbackList;

    /**
     * Ruft den Wert der result-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ResultType }
     *     
     */
    public ResultType getResult() {
        return result;
    }

    /**
     * Legt den Wert der result-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ResultType }
     *     
     */
    public void setResult(ResultType value) {
        this.result = value;
    }

    /**
     * Ruft den Wert der feedbackList-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link FeedbackListType }
     *     
     */
    public FeedbackListType getFeedbackList() {
        return feedbackList;
    }

    /**
     * Legt den Wert der feedbackList-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link FeedbackListType }
     *     
     */
    public void setFeedbackList(FeedbackListType value) {
        this.feedbackList = value;
    }

}
