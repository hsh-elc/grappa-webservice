
package proforma.xml21;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for feedback-level-type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="feedback-level-type">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="debug"/>
 *     &lt;enumeration value="info"/>
 *     &lt;enumeration value="warn"/>
 *     &lt;enumeration value="error"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 */
@XmlType(name = "feedback-level-type", namespace = "urn:proforma:v2.1")
@XmlEnum
public enum FeedbackLevelType {

    @XmlEnumValue("debug")
    DEBUG("debug"),
    @XmlEnumValue("info")
    INFO("info"),
    @XmlEnumValue("warn")
    WARN("warn"),
    @XmlEnumValue("error")
    ERROR("error");
    private final String value;

    FeedbackLevelType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static FeedbackLevelType fromValue(String v) {
        for (FeedbackLevelType c : FeedbackLevelType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
