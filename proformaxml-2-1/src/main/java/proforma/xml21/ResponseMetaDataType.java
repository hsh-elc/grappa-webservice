
package proforma.xml21;

import org.w3c.dom.Element;

import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for response-meta-data-type complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="response-meta-data-type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="response-datetime" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="grader-engine" type="{urn:proforma:v2.1}grader-engine-type"/>
 *         &lt;any processContents='lax' namespace='##other' maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "response-meta-data-type", namespace = "urn:proforma:v2.1", propOrder = {
    "responseDatetime",
    "graderEngine",
    "any"
})
public class ResponseMetaDataType {

    @XmlElement(name = "response-datetime", namespace = "urn:proforma:v2.1")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar responseDatetime;
    @XmlElement(name = "grader-engine", namespace = "urn:proforma:v2.1", required = true)
    protected GraderEngineType graderEngine;
    @XmlAnyElement(lax = true)
    protected List<Object> any;

    /**
     * Gets the value of the responseDatetime property.
     *
     * @return possible object is
     * {@link XMLGregorianCalendar }
     */
    public XMLGregorianCalendar getResponseDatetime() {
        return responseDatetime;
    }

    /**
     * Sets the value of the responseDatetime property.
     *
     * @param value allowed object is
     *              {@link XMLGregorianCalendar }
     */
    public void setResponseDatetime(XMLGregorianCalendar value) {
        this.responseDatetime = value;
    }

    /**
     * Gets the value of the graderEngine property.
     *
     * @return possible object is
     * {@link GraderEngineType }
     */
    public GraderEngineType getGraderEngine() {
        return graderEngine;
    }

    /**
     * Sets the value of the graderEngine property.
     *
     * @param value allowed object is
     *              {@link GraderEngineType }
     */
    public void setGraderEngine(GraderEngineType value) {
        this.graderEngine = value;
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

}
