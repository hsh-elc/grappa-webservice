package de.hsh.grappa.proforma;

import java.io.Serializable;

public class SubmissionResource extends ProformaResource implements Serializable {
    public SubmissionResource(byte[] submissionContent, MimeType mimeType) {
        super(submissionContent, mimeType);
    }
}
