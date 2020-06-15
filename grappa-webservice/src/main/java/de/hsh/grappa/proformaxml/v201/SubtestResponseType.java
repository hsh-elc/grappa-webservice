
package de.hsh.grappa.proformaxml.v201;

import javax.xml.bind.annotation.*;


/**
 * <p>Java-Klasse f�r subtest-response-type complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="subtest-response-type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="test-result" type="{urn:proforma:v2.0.1}test-result-type"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "subtest-response-type", namespace = "urn:proforma:v2.0.1", propOrder = {
    "testResult"
})
public class SubtestResponseType {

    @XmlElement(name = "test-result", namespace = "urn:proforma:v2.0.1", required = true)
    protected TestResultType testResult;
    @XmlAttribute(name = "id", required = true)
    protected String id;

    /**
     * Ruft den Wert der testResult-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link TestResultType }
     *     
     */
    public TestResultType getTestResult() {
        return testResult;
    }

    /**
     * Legt den Wert der testResult-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link TestResultType }
     *     
     */
    public void setTestResult(TestResultType value) {
        this.testResult = value;
    }

    /**
     * Ruft den Wert der id-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Legt den Wert der id-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

}
