
package de.hsh.grappa.proformaxml.v300;

import javax.xml.bind.annotation.*;
import java.math.BigDecimal;


/**
 * <p>Java-Klasse f�r result-type complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="result-type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="score" type="{urn:proforma:v3.0.0}score-type"/>
 *         &lt;element name="validity" type="{urn:proforma:v3.0.0}validity-type" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="is-internal-error" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "result-type", namespace = "urn:proforma:v3.0.0", propOrder = {
    "score",
    "validity"
})
public class ResultType {

    @XmlElement(namespace = "urn:proforma:v3.0.0", required = true)
    protected BigDecimal score;
    @XmlElement(namespace = "urn:proforma:v3.0.0")
    protected BigDecimal validity;
    @XmlAttribute(name = "is-internal-error")
    protected Boolean isInternalError;

    /**
     * Ruft den Wert der score-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getScore() {
        return score;
    }

    /**
     * Legt den Wert der score-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setScore(BigDecimal value) {
        this.score = value;
    }

    /**
     * Ruft den Wert der validity-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getValidity() {
        return validity;
    }

    /**
     * Legt den Wert der validity-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setValidity(BigDecimal value) {
        this.validity = value;
    }

    /**
     * Ruft den Wert der isInternalError-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isIsInternalError() {
        if (isInternalError == null) {
            return false;
        } else {
            return isInternalError;
        }
    }

    /**
     * Legt den Wert der isInternalError-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsInternalError(Boolean value) {
        this.isInternalError = value;
    }

}
