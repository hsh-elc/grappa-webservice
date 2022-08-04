
package proforma.xml21;

import org.w3c.dom.Element;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for feedback-type complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="feedback-type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="title" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="content" minOccurs="0">
 *           &lt;complexType>
 *             &lt;simpleContent>
 *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *                 &lt;attribute name="format" use="required">
 *                   &lt;simpleType>
 *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                       &lt;enumeration value="html"/>
 *                       &lt;enumeration value="plaintext"/>
 *                     &lt;/restriction>
 *                   &lt;/simpleType>
 *                 &lt;/attribute>
 *               &lt;/extension>
 *             &lt;/simpleContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="filerefs" type="{urn:proforma:v2.1}filerefs-type" minOccurs="0"/>
 *         &lt;any processContents='lax' namespace='##other' maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="level" type="{urn:proforma:v2.1}feedback-level-type" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "feedback-type", namespace = "urn:proforma:v2.1", propOrder = {
    "title",
    "content",
    "filerefs",
    "any"
})
public class FeedbackType {

    @XmlElement(namespace = "urn:proforma:v2.1")
    protected String title;
    @XmlElement(namespace = "urn:proforma:v2.1")
    protected FeedbackType.Content content;
    @XmlElement(namespace = "urn:proforma:v2.1")
    protected FilerefsType filerefs;
    @XmlAnyElement(lax = true)
    protected List<Object> any;
    @XmlAttribute(name = "level")
    protected FeedbackLevelType level;

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
     * Gets the value of the content property.
     *
     * @return possible object is
     * {@link FeedbackType.Content }
     */
    public FeedbackType.Content getContent() {
        return content;
    }

    /**
     * Sets the value of the content property.
     *
     * @param value allowed object is
     *              {@link FeedbackType.Content }
     */
    public void setContent(FeedbackType.Content value) {
        this.content = value;
    }

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
     * Gets the value of the level property.
     *
     * @return possible object is
     * {@link FeedbackLevelType }
     */
    public FeedbackLevelType getLevel() {
        return level;
    }

    /**
     * Sets the value of the level property.
     *
     * @param value allowed object is
     *              {@link FeedbackLevelType }
     */
    public void setLevel(FeedbackLevelType value) {
        this.level = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     *
     * <p>The following schema fragment specifies the expected content contained within this class.
     *
     * <pre>
     * &lt;complexType>
     *   &lt;simpleContent>
     *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
     *       &lt;attribute name="format" use="required">
     *         &lt;simpleType>
     *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *             &lt;enumeration value="html"/>
     *             &lt;enumeration value="plaintext"/>
     *           &lt;/restriction>
     *         &lt;/simpleType>
     *       &lt;/attribute>
     *     &lt;/extension>
     *   &lt;/simpleContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "value"
    })
    public static class Content {

        @XmlValue
        protected String value;
        @XmlAttribute(name = "format", required = true)
        protected String format;

        /**
         * Gets the value of the value property.
         *
         * @return possible object is
         * {@link String }
         */
        public String getValue() {
            return value;
        }

        /**
         * Sets the value of the value property.
         *
         * @param value allowed object is
         *              {@link String }
         */
        public void setValue(String value) {
            this.value = value;
        }

        /**
         * Gets the value of the format property.
         *
         * @return possible object is
         * {@link String }
         */
        public String getFormat() {
            return format;
        }

        /**
         * Sets the value of the format property.
         *
         * @param value allowed object is
         *              {@link String }
         */
        public void setFormat(String value) {
            this.format = value;
        }

    }

}
