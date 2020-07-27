package de.hsh.grappa.proforma;

import java.io.Serializable;

public abstract class ProformaObject implements Serializable {
    private final byte[] content;
    private final MimeType mimeType;

    public ProformaObject(byte[] content, MimeType mimeType) {
        if(null == content)
            throw new IllegalArgumentException("content byte array parameter must not be null.");
        if(0 == content.length)
            throw new IllegalArgumentException("content byte array is empty.");
        this.content = content;
        if(null == mimeType)
            throw new IllegalArgumentException("mimeType parameter must not be null.");
        this.mimeType = mimeType;
    }

    public byte[] getContent() {
        return content;
    }

//    public void setContent(byte[] content) {
//        this.content = content;
//    }

    public MimeType getMimeType() {
        return mimeType;
    }

//    public void setMimeType(MimeType mimeType) {
//        this.mimeType = mimeType;
//    }

    @Override
    public String toString() {
        return String.format("%s{content=byte[%d], mimeType=%s}",
            getClass().getSimpleName(), content.length, mimeType);
    }
}
