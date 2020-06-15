package de.hsh.grappa.proforma;

import java.io.Serializable;

// Instances of this class are cached by redis.
public class ProformaTask extends ProformaObject implements Serializable {
    public ProformaTask(byte[] taskContent, MimeType mimeType) {
        super(taskContent, mimeType);
    }
}
