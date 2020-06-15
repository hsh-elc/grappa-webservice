
package de.hsh.grappa.proformaxml.v201;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java-Klasse f�r filerefs-type complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="filerefs-type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence maxOccurs="unbounded">
 *         &lt;element name="fileref" type="{urn:proforma:v2.0.1}fileref-type"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "filerefs-type", namespace = "urn:proforma:v2.0.1", propOrder = {
    "fileref"
})
public class FilerefsType {

    @XmlElement(namespace = "urn:proforma:v2.0.1", required = true)
    protected List<FilerefType> fileref;

    /**
     * Gets the value of the fileref property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the fileref property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFileref().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FilerefType }
     * 
     * 
     */
    public List<FilerefType> getFileref() {
        if (fileref == null) {
            fileref = new ArrayList<FilerefType>();
        }
        return this.fileref;
    }

}
