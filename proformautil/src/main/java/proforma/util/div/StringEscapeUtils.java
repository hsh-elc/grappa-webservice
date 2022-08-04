package proforma.util.div;

/**
 * This is a wrapper around
 * org.apache.commons.text.StringEscapeUtils
 */
public class StringEscapeUtils {
    public static String escapeHtml4(String s) {
        return org.apache.commons.text.StringEscapeUtils.escapeHtml4(s);
    }

}
