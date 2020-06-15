package de.hsh.grappa.proforma;

import java.io.Serializable;

public class ProformaResponse extends ProformaObject implements Serializable {
    public ProformaResponse(byte[] responseContent, MimeType mimeType) {
        super(responseContent, mimeType);
    }
}
