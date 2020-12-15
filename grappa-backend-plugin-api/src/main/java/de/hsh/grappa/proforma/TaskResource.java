package de.hsh.grappa.proforma;

import java.io.Serializable;

public class TaskResource extends ProformaResource implements Serializable {
    public TaskResource(byte[] taskContent, MimeType mimeType) {
        super(taskContent, mimeType);
    }
}
