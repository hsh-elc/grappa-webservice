
package de.hsh.grappa.proformaxml.v300;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java-Klasse f�r model-solutions-type complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="model-solutions-type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence maxOccurs="unbounded">
 *         &lt;element name="model-solution" type="{urn:proforma:v3.0.0}model-solution-type"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "model-solutions-type", namespace = "urn:proforma:v3.0.0", propOrder = {
    "modelSolution"
})
public class ModelSolutionsType {

    @XmlElement(name = "model-solution", namespace = "urn:proforma:v3.0.0", required = true)
    protected List<ModelSolutionType> modelSolution;

    /**
     * Gets the value of the modelSolution property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the modelSolution property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getModelSolution().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ModelSolutionType }
     * 
     * 
     */
    public List<ModelSolutionType> getModelSolution() {
        if (modelSolution == null) {
            modelSolution = new ArrayList<ModelSolutionType>();
        }
        return this.modelSolution;
    }

}
