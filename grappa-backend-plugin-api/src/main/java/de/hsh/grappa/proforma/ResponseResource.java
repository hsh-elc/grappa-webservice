package de.hsh.grappa.proforma;

import java.io.Serializable;

public class ResponseResource extends ProformaResource implements Serializable {
    public ResponseResource(byte[] responseContent, MimeType mimeType) {
        super(responseContent, mimeType);
    }
}
