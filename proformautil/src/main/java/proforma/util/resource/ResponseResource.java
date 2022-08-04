package proforma.util.resource;

import java.io.Serializable;

public class ResponseResource extends ProformaResource implements Serializable {
    private static final long serialVersionUID = 1L;

    public ResponseResource(byte[] responseContent, MimeType mimeType) {
        super(responseContent, mimeType);
    }

    public ResponseResource(byte[] content) {
        super(content);
    }
}
