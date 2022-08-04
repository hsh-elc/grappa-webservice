
package proforma.xml21;

import org.w3c.dom.Element;

import javax.xml.bind.annotation.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for test-configuration-type complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="test-configuration-type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="filerefs" type="{urn:proforma:v2.1}filerefs-type" minOccurs="0"/>
 *         &lt;element name="timeout" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}positiveInteger">
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="externalresourcerefs" type="{urn:proforma:v2.1}externalresourcerefs-type" minOccurs="0"/>
 *         &lt;any processContents='lax' namespace='##other' maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="test-meta-data" type="{urn:proforma:v2.1}test-meta-data-type" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "test-configuration-type", namespace = "urn:proforma:v2.1", propOrder = {
    "filerefs",
    "timeout",
    "externalresourcerefs",
    "any",
    "testMetaData"
})
public class TestConfigurationType {

    @XmlElement(namespace = "urn:proforma:v2.1")
    protected FilerefsType filerefs;
    @XmlElement(namespace = "urn:proforma:v2.1")
    protected BigInteger timeout;
    @XmlElement(namespace = "urn:proforma:v2.1")
    protected ExternalresourcerefsType externalresourcerefs;
    @XmlAnyElement(lax = true)
    protected List<Object> any;
    @XmlElement(name = "test-meta-data", namespace = "urn:proforma:v2.1")
    protected TestMetaDataType testMetaData;

    /**
     * Gets the value of the filerefs property.
     *
     * @return possible object is
     * {@link FilerefsType }
     */
    public FilerefsType getFilerefs() {
        return filerefs;
    }

    /**
     * Sets the value of the filerefs property.
     *
     * @param value allowed object is
     *              {@link FilerefsType }
     */
    public void setFilerefs(FilerefsType value) {
        this.filerefs = value;
    }

    /**
     * Gets the value of the timeout property.
     *
     * @return possible object is
     * {@link BigInteger }
     */
    public BigInteger getTimeout() {
        return timeout;
    }

    /**
     * Sets the value of the timeout property.
     *
     * @param value allowed object is
     *              {@link BigInteger }
     */
    public void setTimeout(BigInteger value) {
        this.timeout = value;
    }

    /**
     * Gets the value of the externalresourcerefs property.
     *
     * @return possible object is
     * {@link ExternalresourcerefsType }
     */
    public ExternalresourcerefsType getExternalresourcerefs() {
        return externalresourcerefs;
    }

    /**
     * Sets the value of the externalresourcerefs property.
     *
     * @param value allowed object is
     *              {@link ExternalresourcerefsType }
     */
    public void setExternalresourcerefs(ExternalresourcerefsType value) {
        this.externalresourcerefs = value;
    }

    /**
     * Gets the value of the any property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the any property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAny().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * {@link Element }
     */
    public List<Object> getAny() {
        if (any == null) {
            any = new ArrayList<Object>();
        }
        return this.any;
    }

    /**
     * Gets the value of the testMetaData property.
     *
     * @return possible object is
     * {@link TestMetaDataType }
     */
    public TestMetaDataType getTestMetaData() {
        return testMetaData;
    }

    /**
     * Sets the value of the testMetaData property.
     *
     * @param value allowed object is
     *              {@link TestMetaDataType }
     */
    public void setTestMetaData(TestMetaDataType value) {
        this.testMetaData = value;
    }

}
