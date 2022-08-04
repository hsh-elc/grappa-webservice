package proforma.util;


import proforma.util.div.PropertyHandle;
import proforma.util.div.Strings;
import proforma.util.div.Zip;
import proforma.util.div.Zip.ZipContent;
import proforma.util.div.Zip.ZipContentElement;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * <p>An instance of a subclass can interact with a single file
 * that is stored as part of a ProFormA task, submission, or response.</p>
 *
 * <p>The interface abstracts from the ProFormA version by returning and accepting
 * abstract "handles" to interact with embedded or attached / binary or text attachments.</p>
 */
public abstract class ProformaFileHandle {

    private Object file;
    private ZipContent zipContent;
    private String pathPrefixInsideZip;
    private PropertyHandle idHandle;
    private PropertyHandle mimetypeHandle;

    /**
     * @param file
     * @param zipContent
     * @param pathPrefixInsideZip this is either the empty string or a relative path ending
     *                            with '/'. The path uses '/' as dir separator.
     *                            An attached file's filename will be
     *                            evaluated relative to this path.
     */
    protected ProformaFileHandle(Object file, ZipContent zipContent, String pathPrefixInsideZip) {
        if (file == null) throw new AssertionError(this.getClass() + ": file shouldn't be null");
        this.file = file;
        this.zipContent = zipContent;
        this.pathPrefixInsideZip = pathPrefixInsideZip;
        this.idHandle = new PropertyHandle(file, "id", String.class);
        this.mimetypeHandle = new PropertyHandle(file, "mimetype", String.class);
    }


    public Object getFile() {
        return file;
    }

    public String getId() {
        return (String) idHandle.get();
    }

    public void setId(String id) {
        idHandle.set(id);
    }


    public String getMimetype() {
        return (String) mimetypeHandle.get();
    }

    public void setMimetype(String mimetype) {
        mimetypeHandle.set(mimetype);
    }

    /**
     * @return a writable map
     */
    public ZipContent getZipContent() {
        return zipContent;
    }

    /**
     * In case of an attached file that is part of a submission,
     * the file is contained in a subfolder (e. g. "task" or "submission").
     *
     * @return the folder or path, that the file is contained in. The prefix must be
     * prepended to any attached file to get the real path inside the zip.
     * The return value is either the empty string or it ends with a forward slash.
     * The returned path uses the forward slash as the dir separator.
     */
    public String getPathPrefixInsideZip() {
        return pathPrefixInsideZip;
    }


    /**
     * Usage (e. g. modify the filename):
     * <pre>
     *   ProformaFileHandle pfh = ...;
     *   if (pfh.embeddedTxtFileHandle().get() != null) {
     *       String filename = pfh.embeddedTxtFileHandle().getFilename();
     *       filename = ...
     *       pfh.embeddedTxtFileHandle().setFilename(filename);
     *   }
     * </pre>
     *
     * @return the returned handle provides service routines for further processing
     * of an embedded text file (if any).
     */
    public abstract ProformaEmbeddedTxtFileHandle embeddedTxtFileHandle();

    /**
     * Usage:
     * <pre>
     *   ProformaFileHandle pfh = ...;
     *   if (pfh.embeddedBinFileHandle().get() != null) {
     *       byte[] bytes = pfh.embeddedBinFileHandle().getContent();
     *       ... manipulate the bytes anyhow ...
     *       pfh.embeddedBinFileHandle().setContent(bytes);
     *   }
     * </pre>
     *
     * @return the returned handle provides service routines for further processing
     * of an embedded binary file (if any).
     */
    public abstract ProformaEmbeddedBinFileHandle embeddedBinFileHandle();

    /**
     * Usage:
     * <pre>
     *   ProformaFileHandle pfh = ...;
     *   if (pfh.attachedTxtFileHandle().get() != null) {
     *       String path = pfh.attachedTxtFileHandle().getPath();
     *       if ( ... path is invalid ...) {
     *           pfh.attachedTxtFileHandle().remove();
     *           // ^ this will remove the attached txt file from the pojo only.
     *           // now we remove the file from the zip content as well:
     *           pfh.getZipContent().remove(path);
     *       }
     *   }
     * </pre>
     *
     * @return the returned handle provides service routines for further processing
     * of an attached text file (if any).
     */
    public abstract ProformaAttachedTxtFileHandle attachedTxtFileHandle();

    /**
     * Usage (convert attached binary to text file):
     * <pre>
     *   ProformaFileHandle pfh = ...;
     *   String encoding = ...;
     *   if (pfh.attachedBinFileHandle().get() != null) {
     *       String path = pfh.attachedBinFileHandle().getPath();
     *       try {
     *           byte[] bytes = pfh.getZipContent().get(path).getBytes();
     *           String text = new String(bytes, encoding);
     *       } catch (Exception e) {
     *           // bytes cannot be processed as text in the given encoding
     *       }
     *       pfh.attachedTxtFileHandle().createAndSet()
     *           .setEncoding(encoding).setPath(path);
     *       pfh.attachedBinFileHandle().remove();
     *   }
     * </pre>
     *
     * @return the returned handle provides service routines for further processing
     * of an attached binary file (if any).
     */
    public abstract ProformaAttachedBinFileHandle attachedBinFileHandle();


    /**
     * If this is an embedded binary file that represents UTF-8 encoded text
     * (as guessed by {@link Strings#looksLikeUTF8(byte[])}), the file is embedded as a text file.
     * Otherwise nothing happens.
     *
     * @return true, if the file was converted
     */
    private boolean convertEmbeddedFileFromBinToText() {
        if (embeddedBinFileHandle().get() != null) {
            byte[] b = embeddedBinFileHandle().getContent();
            if (Strings.looksLikeUTF8(b)) {
                String fileContent = new String(b, StandardCharsets.UTF_8);
                embeddedTxtFileHandle().createAndSet()
                    .setFilename(embeddedBinFileHandle().getFilename())
                    .setContent(fileContent);
                embeddedBinFileHandle().remove();
                return true;
            }
        }
        return false;
    }

    /**
     * If this is an embedded binary file whose bytes represent
     * a zip file with a single contained file in it,
     * then this method unzips that single file and puts this
     * replacing the original embedded binary file.
     * Otherwise nothing happens.
     *
     * @return true, if there has been any conversion.
     */
    private boolean unzipEmbeddedSingleZip() {
        if (embeddedBinFileHandle().get() != null) {
            byte[] b = embeddedBinFileHandle().getContent();
            ZipContentElement e = Zip.unzipSingleOrNull(b);
            if (e != null) {
                b = e.getBytes();
                embeddedBinFileHandle().setContent(b);
                return true;
            }
        }
        return false;
    }

    /**
     * If this is an attached binary file whose bytes represent
     * a zip file with a single contained file in it,
     * <ul>
     * <li>
     *   then this method unzips that single file and puts this
     *   replacing the original attached binary file. The new path is the path of the nested file
     *   with {@link #pathPrefixInsideZip} prepended.</li>
     * <li>
     *   Otherwise nothing happens.</li>
     * </ul>
     *
     * @return true, if there has been any conversion.
     */
    private boolean unzipAttachedSingleZip() {
        if (attachedBinFileHandle().get() != null) {
            String fullAttachedFilePath = getPathPrefixInsideZip() + attachedBinFileHandle().getPath();
            if (!getZipContent().containsKey(fullAttachedFilePath)) {
                throw new IllegalArgumentException("There is no file '" + fullAttachedFilePath + "' inside the zip file");
            }
            ZipContentElement zipFileContent = getZipContent().get(fullAttachedFilePath);
            byte[] b = zipFileContent.getBytes();
            ZipContentElement nestedZipElement = Zip.unzipSingleOrNull(b);
            if (nestedZipElement != null) {
                getZipContent().remove(fullAttachedFilePath);
                String newFileName = nestedZipElement.getPath();
                attachedBinFileHandle().createAndSet()
                    .setPath(newFileName);
                String newFullAttachedFilePath = getPathPrefixInsideZip() + newFileName;
                ZipContentElement newElem = new ZipContentElement(newFullAttachedFilePath, nestedZipElement.getBytes(), nestedZipElement.getTime());
                getZipContent().put(newFullAttachedFilePath, newElem);
                return true;
            }
        }
        return false;
    }

    private boolean convertEmbeddedBinToTxt(boolean unzipSingleZipAttachments, boolean convertUTF8BytesToText) {
        boolean result = false;
        if (unzipSingleZipAttachments) {
            result = result | unzipEmbeddedSingleZip();
        }
        if (convertUTF8BytesToText) {
            result = result | convertEmbeddedFileFromBinToText();
        }
        return result;
    }

    private void convertAttachedBinToEmbedded(boolean unzipSingleZipAttachments, boolean convertUTF8BytesToText) {
        if (unzipSingleZipAttachments) {
            unzipAttachedSingleZip();
        }
        String attachedFileName = attachedBinFileHandle().getPath();

        String fullAttachedFilePath = getPathPrefixInsideZip() + attachedFileName;

        // get file content
        if (!getZipContent().containsKey(fullAttachedFilePath)) {
            throw new IllegalArgumentException("There is no file '" + fullAttachedFilePath + "' inside the zip file");
        }
        ZipContentElement zipFileContent = getZipContent().get(fullAttachedFilePath);
        byte[] b = zipFileContent.getBytes();

        if (convertUTF8BytesToText && Strings.looksLikeUTF8(b)) {
            String fileContent = new String(b, StandardCharsets.UTF_8);
            embeddedTxtFileHandle().createAndSet()
                .setFilename(attachedFileName)
                .setContent(fileContent);
        } else {
            embeddedBinFileHandle().createAndSet()
                .setFilename(attachedFileName)
                .setContent(b);
        }
        getZipContent().remove(fullAttachedFilePath);
        attachedBinFileHandle().remove();
    }

    private void convertAttachedTxtToEmbedded() {
        String attachedFileName = attachedTxtFileHandle().getPath();

        String fullAttachedFilePath = getPathPrefixInsideZip() + attachedFileName;

        // get file content
        if (!getZipContent().containsKey(fullAttachedFilePath)) {
            throw new IllegalArgumentException("There is no file '" + fullAttachedFilePath + "' inside the zip file");
        }
        ZipContentElement zipFileContent = getZipContent().get(fullAttachedFilePath);
        byte[] b = zipFileContent.getBytes();

        Charset cs = StandardCharsets.UTF_8;
        if (!Strings.isNullOrEmpty(attachedTxtFileHandle().getEncoding())) {
            cs = Charset.forName(attachedTxtFileHandle().getEncoding());
        }
        String fileContent = new String(b, cs);
        embeddedTxtFileHandle().createAndSet()
            .setFilename(attachedFileName)
            .setContent(fileContent);

        getZipContent().remove(fullAttachedFilePath);
        attachedTxtFileHandle().remove();
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
    protected boolean convertToEmbedded(boolean unzipSingleZipAttachments, boolean convertUTF8BytesToText) {

        if (null != embeddedTxtFileHandle().get()) {
            //if embedded directly skip
            return false;
        }

        if (null != embeddedBinFileHandle().get()) {
            return convertEmbeddedBinToTxt(unzipSingleZipAttachments, convertUTF8BytesToText);
        }

        if (null != attachedBinFileHandle().get()) {
            convertAttachedBinToEmbedded(unzipSingleZipAttachments, convertUTF8BytesToText);
            return true;
        }

        if (null != attachedTxtFileHandle().get()) {
            convertAttachedTxtToEmbedded();
            return true;
        }

        throw new UnsupportedOperationException("file is neither attached nor embedded type");
    }


}
