
package de.hsh.grappa.proformaxml.v300;

import javax.xml.bind.annotation.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java-Klasse f�r submission-restrictions-type complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="submission-restrictions-type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *         &lt;element name="file-restriction" type="{urn:proforma:v3.0.0}file-restr-type"/>
 *       &lt;/sequence>
 *       &lt;attribute name="max-size" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "submission-restrictions-type", namespace = "urn:proforma:v3.0.0", propOrder = {
    "fileRestriction"
})
public class SubmissionRestrictionsType {

    @XmlElement(name = "file-restriction", namespace = "urn:proforma:v3.0.0")
    protected List<FileRestrType> fileRestriction;
    @XmlAttribute(name = "max-size")
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger maxSize;

    /**
     * Gets the value of the fileRestriction property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the fileRestriction property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFileRestriction().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FileRestrType }
     * 
     * 
     */
    public List<FileRestrType> getFileRestriction() {
        if (fileRestriction == null) {
            fileRestriction = new ArrayList<FileRestrType>();
        }
        return this.fileRestriction;
    }

    /**
     * Ruft den Wert der maxSize-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getMaxSize() {
        return maxSize;
    }

    /**
     * Legt den Wert der maxSize-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setMaxSize(BigInteger value) {
        this.maxSize = value;
    }

}
