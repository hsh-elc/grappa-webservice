package proforma.util.resource;

import proforma.util.div.XmlUtils;
import proforma.util.div.Zip;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Stream;

/**
 * The base class for the three different Proforma types,
 * Task, Submission, and Response.
 */
public abstract class ProformaResource implements Serializable {
    private static final long serialVersionUID = 1L;
    private byte[] content;
    private MimeType mimeType;

    public ProformaResource(byte[] content, MimeType mimeType) {
        if (null == content)
            throw new IllegalArgumentException("content byte array parameter must not be null.");
        if (0 == content.length)
            throw new IllegalArgumentException("content byte array is empty.");
        this.content = content;
        if (null == mimeType)
            throw new IllegalArgumentException("mimeType parameter must not be null.");
        this.mimeType = mimeType;
    }

    protected ProformaResource(byte[] content) {
        this(content, guessMimeType(content));
    }

    public byte[] getContent() {
        return content;
    }

    public String getXmlContent() {
        if (mimeType.equals(MimeType.XML)) {
            return new String(content, StandardCharsets.UTF_8);
        }
        throw new UnsupportedOperationException("Cannot get xml content from a zip resource");
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public MimeType getMimeType() {
        return mimeType;
    }

    public void setMimeType(MimeType mimeType) {
        this.mimeType = mimeType;
    }

    @Override
    public String toString() {
        return String.format("%s{content=byte[%d], mimeType=%s}",
            getClass().getSimpleName(), content.length, mimeType);
    }

    private static MimeType guessMimeType(byte[] bytes) {
        if (Zip.isZip(bytes)) return MimeType.ZIP;
        if (XmlUtils.isXml(bytes)) return MimeType.XML;
        throw new IllegalArgumentException("Cant guess mimetype - neither ZP nor XML detected (first 10 bytes are "
            + Arrays.toString(Stream.of(bytes).limit(10).toArray())
            + ")");
    }
}
