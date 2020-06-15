
package de.hsh.grappa.proformaxml.v201;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java-Klasse f�r subtests-response-type complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="subtests-response-type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="subtest-response" type="{urn:proforma:v2.0.1}subtest-response-type" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "subtests-response-type", namespace = "urn:proforma:v2.0.1", propOrder = {
    "subtestResponse"
})
public class SubtestsResponseType {

    @XmlElement(name = "subtest-response", namespace = "urn:proforma:v2.0.1", required = true)
    protected List<SubtestResponseType> subtestResponse;

    /**
     * Gets the value of the subtestResponse property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the subtestResponse property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSubtestResponse().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SubtestResponseType }
     * 
     * 
     */
    public List<SubtestResponseType> getSubtestResponse() {
        if (subtestResponse == null) {
            subtestResponse = new ArrayList<SubtestResponseType>();
        }
        return this.subtestResponse;
    }

}
