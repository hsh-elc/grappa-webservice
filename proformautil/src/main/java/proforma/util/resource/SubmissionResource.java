package proforma.util.resource;

import java.io.Serializable;

public class SubmissionResource extends ProformaResource implements Serializable {
    private static final long serialVersionUID = 1L;

    public SubmissionResource(byte[] submissionContent, MimeType mimeType) {
        super(submissionContent, mimeType);
    }

    public SubmissionResource(byte[] content) {
        super(content);
    }
}
