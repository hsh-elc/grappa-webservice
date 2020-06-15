
package de.hsh.grappa.proformaxml.v201;

import javax.xml.bind.annotation.*;


/**
 * <p>Java-Klasse f�r feedback-type complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
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
 *         &lt;element name="filerefs" type="{urn:proforma:v2.0.1}filerefs-type" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="level" type="{urn:proforma:v2.0.1}feedback-level-type" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "feedback-type", namespace = "urn:proforma:v2.0.1", propOrder = {
    "title",
    "content",
    "filerefs"
})
public class FeedbackType {

    @XmlElement(namespace = "urn:proforma:v2.0.1")
    protected String title;
    @XmlElement(namespace = "urn:proforma:v2.0.1")
    protected FeedbackType.Content content;
    @XmlElement(namespace = "urn:proforma:v2.0.1")
    protected FilerefsType filerefs;
    @XmlAttribute(name = "level")
    protected FeedbackLevelType level;

    /**
     * Ruft den Wert der title-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTitle() {
        return title;
    }

    /**
     * Legt den Wert der title-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTitle(String value) {
        this.title = value;
    }

    /**
     * Ruft den Wert der content-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link FeedbackType.Content }
     *     
     */
    public FeedbackType.Content getContent() {
        return content;
    }

    /**
     * Legt den Wert der content-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link FeedbackType.Content }
     *     
     */
    public void setContent(FeedbackType.Content value) {
        this.content = value;
    }

    /**
     * Ruft den Wert der filerefs-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link FilerefsType }
     *     
     */
    public FilerefsType getFilerefs() {
        return filerefs;
    }

    /**
     * Legt den Wert der filerefs-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link FilerefsType }
     *     
     */
    public void setFilerefs(FilerefsType value) {
        this.filerefs = value;
    }

    /**
     * Ruft den Wert der level-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link FeedbackLevelType }
     *     
     */
    public FeedbackLevelType getLevel() {
        return level;
    }

    /**
     * Legt den Wert der level-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link FeedbackLevelType }
     *     
     */
    public void setLevel(FeedbackLevelType value) {
        this.level = value;
    }


    /**
     * <p>Java-Klasse f�r anonymous complex type.
     * 
     * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
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
     * 
     * 
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
         * Ruft den Wert der value-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setValue(String value) {
            this.value = value;
        }

        /**
         * Ruft den Wert der format-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getFormat() {
            return format;
        }

        /**
         * Legt den Wert der format-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setFormat(String value) {
            this.format = value;
        }

    }

}
