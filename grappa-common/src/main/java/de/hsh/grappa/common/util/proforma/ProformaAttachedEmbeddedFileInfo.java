package de.hsh.grappa.common.util.proforma;

/**
 * Data of an attached or embedded file
 */
public class ProformaAttachedEmbeddedFileInfo {
    private String id;
    private String mimetype;
    private String filename;
    private byte[] binContent;
    private String txtContent;
    public ProformaAttachedEmbeddedFileInfo(String id, String mimetype, String filename, byte[] binContent) {
        this.id = id;
        this.mimetype = mimetype;
        this.filename = filename;
        this.binContent = binContent;
    }
    public ProformaAttachedEmbeddedFileInfo(String id, String mimetype, String filename, String txtContent) {
        this.id = id;
        this.mimetype = mimetype;
        this.filename = filename;
        this.txtContent = txtContent;
    }
    
    public String getId() {
        return id;
    }
    public String getMimetype() {
        return mimetype;
    }
    public String getFilename() {
        return filename;
    }
    public byte[] getBinContent() {
        return binContent;
    }
    public String getTxtContent() {
        return txtContent;
    }
}