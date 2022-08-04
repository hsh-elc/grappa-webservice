package proforma.util.resource;

import java.io.Serializable;

public class TaskResource extends ProformaResource implements Serializable {
    private static final long serialVersionUID = 1L;

    public TaskResource(byte[] taskContent, MimeType mimeType) {
        super(taskContent, mimeType);
    }

    public TaskResource(byte[] content) {
        super(content);
    }
}
