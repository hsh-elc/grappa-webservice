package proforma.util;

import proforma.util.div.PropertyHandle;
import proforma.util.div.Strings;
import proforma.util.div.Zip.ZipContent;

/**
 * <p>An instance of a subclass can interact with
 * a file that is embedded or attached in a task.</p>
 *
 * <p>This abstract class is independent of a specific ProFormA version.
 * Subclasses are version specific.</p>
 */
public abstract class ProformaTaskFileHandle extends ProformaFileHandle {

    private PropertyHandle visibleHandle;
    private PropertyHandle usageByLmsHandle;
    private PropertyHandle usedByGraderHandle;

    protected ProformaTaskFileHandle(Object file, ZipContent zipContent) {
        super(file, zipContent, "");
        visibleHandle = new PropertyHandle(file, "visible", String.class);
        usageByLmsHandle = new PropertyHandle(file, "usageByLms", String.class);
        usedByGraderHandle = new PropertyHandle(file, "usedByGrader", Boolean.class);
    }


    public boolean isUsedByGrader() {
        return (boolean) usedByGraderHandle.get();
    }

    public void setUsedByGrader(boolean value) {
        usedByGraderHandle.set(value);
    }

    public String getVisible() {
        return (String) visibleHandle.get();
    }

    public void setVisible(String value) {
        visibleHandle.set(value);
    }

    public String getUsageByLms() {
        return (String) usageByLmsHandle.get();
    }

    public void setUsageByLms(String value) {
        usageByLmsHandle.set(value);
    }


    /**
     * If this is an attached file, this method converts it to an embedded file. The original
     * file is replaced.
     * If this is an embedded binary file, this method converts it to an embedded text file depending
     * on the actual parameter values for {@code unzipSingleZipAttachments} and {@code convertUTF8BytesToText}.
     *
     * @param unzipSingleZipAttachments If this is true, then this method checks, if the attached or embedded
     *                                  binary file is a zip file with a
     *                                  single file in it. Then, the file will be extracted before embedding it.
     * @param convertUTF8BytesToText    If this is true, this method checks, if an attached or embedded binary
     *                                  file represents UTF-8 encoded text
     *                                  (as guessed by {@link Strings#looksLikeUTF8(byte[])}). Then, the file is embedded as a text file.
     * @return true, if file was converted
     */
    public boolean convertTaskFileToEmbedded(
        boolean unzipSingleZipAttachments, boolean convertUTF8BytesToText) {
        return convertToEmbedded(unzipSingleZipAttachments, convertUTF8BytesToText);
    }
}
