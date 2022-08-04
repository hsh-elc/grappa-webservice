
package proforma.xml21;

import javax.xml.bind.annotation.*;
import java.math.BigDecimal;


/**
 * <p>Java class for result-type complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="result-type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="score" type="{urn:proforma:v2.1}score-type"/>
 *         &lt;element name="validity" type="{urn:proforma:v2.1}validity-type" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="is-internal-error" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "result-type", namespace = "urn:proforma:v2.1", propOrder = {
    "score",
    "validity"
})
public class ResultType {

    @XmlElement(namespace = "urn:proforma:v2.1", required = true)
    protected BigDecimal score;
    @XmlElement(namespace = "urn:proforma:v2.1")
    protected BigDecimal validity;
    @XmlAttribute(name = "is-internal-error")
    protected Boolean isInternalError;

    /**
     * Gets the value of the score property.
     *
     * @return possible object is
     * {@link BigDecimal }
     */
    public BigDecimal getScore() {
        return score;
    }

    /**
     * Sets the value of the score property.
     *
     * @param value allowed object is
     *              {@link BigDecimal }
     */
    public void setScore(BigDecimal value) {
        this.score = value;
    }

    /**
     * Gets the value of the validity property.
     *
     * @return possible object is
     * {@link BigDecimal }
     */
    public BigDecimal getValidity() {
        return validity;
    }

    /**
     * Sets the value of the validity property.
     *
     * @param value allowed object is
     *              {@link BigDecimal }
     */
    public void setValidity(BigDecimal value) {
        this.validity = value;
    }

    /**
     * Gets the value of the isInternalError property.
     *
     * @return possible object is
     * {@link Boolean }
     */
    public boolean isIsInternalError() {
        if (isInternalError == null) {
            return false;
        } else {
            return isInternalError;
        }
    }

    /**
     * Sets the value of the isInternalError property.
     *
     * @param value allowed object is
     *              {@link Boolean }
     */
    public void setIsInternalError(Boolean value) {
        this.isInternalError = value;
    }

}
