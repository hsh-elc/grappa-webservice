
package proforma.xml21;

import javax.xml.bind.annotation.*;
import java.math.BigDecimal;


/**
 * <p>Java class for test-type complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="test-type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="title" type="{urn:proforma:v2.1}title-type"/>
 *         &lt;element name="description" type="{urn:proforma:v2.1}description-type" minOccurs="0"/>
 *         &lt;element name="internal-description" type="{urn:proforma:v2.1}description-type" minOccurs="0"/>
 *         &lt;element name="test-type" type="{urn:proforma:v2.1}test-type-type"/>
 *         &lt;element name="test-configuration" type="{urn:proforma:v2.1}test-configuration-type"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="validity" default="1.00">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}decimal">
 *             &lt;totalDigits value="3"/>
 *             &lt;fractionDigits value="2"/>
 *             &lt;minInclusive value="0"/>
 *             &lt;maxInclusive value="1.00"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "test-type", namespace = "urn:proforma:v2.1", propOrder = {
    "title",
    "description",
    "internalDescription",
    "testType",
    "testConfiguration"
})
public class TestType {

    @XmlElement(namespace = "urn:proforma:v2.1", required = true)
    protected String title;
    @XmlElement(namespace = "urn:proforma:v2.1")
    protected String description;
    @XmlElement(name = "internal-description", namespace = "urn:proforma:v2.1")
    protected String internalDescription;
    @XmlElement(name = "test-type", namespace = "urn:proforma:v2.1", required = true)
    protected String testType;
    @XmlElement(name = "test-configuration", namespace = "urn:proforma:v2.1", required = true)
    protected TestConfigurationType testConfiguration;
    @XmlAttribute(name = "id", required = true)
    protected String id;
    @XmlAttribute(name = "validity")
    protected BigDecimal validity;

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
     * Gets the value of the testType property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getTestType() {
        return testType;
    }

    /**
     * Sets the value of the testType property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setTestType(String value) {
        this.testType = value;
    }

    /**
     * Gets the value of the testConfiguration property.
     *
     * @return possible object is
     * {@link TestConfigurationType }
     */
    public TestConfigurationType getTestConfiguration() {
        return testConfiguration;
    }

    /**
     * Sets the value of the testConfiguration property.
     *
     * @param value allowed object is
     *              {@link TestConfigurationType }
     */
    public void setTestConfiguration(TestConfigurationType value) {
        this.testConfiguration = value;
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

    /**
     * Gets the value of the validity property.
     *
     * @return possible object is
     * {@link BigDecimal }
     */
    public BigDecimal getValidity() {
        if (validity == null) {
            return new BigDecimal("1.00");
        } else {
            return validity;
        }
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

}
