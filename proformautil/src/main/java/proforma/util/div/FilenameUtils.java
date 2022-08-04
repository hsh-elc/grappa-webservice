package proforma.util.div;

/**
 * Wrapper around org.apache.commons.io.FilenameUtils
 */
public class FilenameUtils {

    public static String separatorsToUnix(String s) {
        return org.apache.commons.io.FilenameUtils.separatorsToUnix(s);
    }

    public static String getExtension(String filename) {
        return org.apache.commons.io.FilenameUtils.getExtension(filename);
    }

    public static String getBasename(String filename) {
        return org.apache.commons.io.FilenameUtils.getBaseName(filename);
    }

    public static String concat(final String basePath, final String fullFilenameToAdd) {
        return org.apache.commons.io.FilenameUtils.concat(basePath, fullFilenameToAdd);
    }

    public static String getName(String path) {
        return org.apache.commons.io.FilenameUtils.getName(path);
    }
}
