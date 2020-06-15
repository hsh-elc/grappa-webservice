package de.hsh.grappa.proforma;

import java.io.Serializable;

public class ProformaSubmission extends ProformaObject implements Serializable {
    public ProformaSubmission(byte[] submissionContent, MimeType mimeType) {
        super(submissionContent, mimeType);
    }
}
