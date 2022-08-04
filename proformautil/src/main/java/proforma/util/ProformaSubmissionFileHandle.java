package proforma.util;


import proforma.util.div.Strings;
import proforma.util.div.Zip.ZipContent;

/**
 * <p>An instance of a subclass can interact with a single file that has been submitted
 * and that is stored as part of the ProFormA submission.</p>
 *
 * <p>The interface abstracts from the ProFormA version by returning and accepting
 * abstract "handles" to interact with embedded or attached / binary or text attachments.</p>
 */
public abstract class ProformaSubmissionFileHandle extends ProformaFileHandle {

    protected ProformaSubmissionFileHandle(Object file, ZipContent zipContent) {
        super(file, zipContent, ProformaSubmissionZipPathes.SUBMISSION_DIRECTORY + "/");
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
    public boolean convertSubmissionFileToEmbedded(
        boolean unzipSingleZipAttachments, boolean convertUTF8BytesToText) {
        return convertToEmbedded(unzipSingleZipAttachments, convertUTF8BytesToText);
    }

}
