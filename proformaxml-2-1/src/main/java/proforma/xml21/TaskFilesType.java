
package proforma.xml21;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for task-files-type complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="task-files-type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *         &lt;element name="file" type="{urn:proforma:v2.1}task-file-type"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "task-files-type", namespace = "urn:proforma:v2.1", propOrder = {
    "file"
})
public class TaskFilesType {

    @XmlElement(namespace = "urn:proforma:v2.1")
    protected List<TaskFileType> file;

    /**
     * Gets the value of the file property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the file property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFile().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TaskFileType }
     */
    public List<TaskFileType> getFile() {
        if (file == null) {
            file = new ArrayList<TaskFileType>();
        }
        return this.file;
    }

}
