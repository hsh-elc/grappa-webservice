package proforma.util.div;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Wrapper class around IOUtils methods from org.apache.commons.ioIOUtils.
 */
public class IOUtils {

    public static String toString(InputStream is, String charset) throws IOException {
        return org.apache.commons.io.IOUtils.toString(is, charset);
    }

    public static String toString(byte[] bytes, String charset) throws IOException {
        return org.apache.commons.io.IOUtils.toString(bytes, charset);
    }

    public static byte[] toByteArray(InputStream is) throws IOException {
        return org.apache.commons.io.IOUtils.toByteArray(is);
    }

    public static int copy(InputStream is, OutputStream os) throws IOException {
        return org.apache.commons.io.IOUtils.copy(is, os);
    }

}
