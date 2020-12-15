package de.hsh.grappa.proforma;


import de.hsh.grappa.exceptions.GrappaException;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class ResourceDownloader {
    public TaskResource downloadTaskResource(String url) throws Exception {
        MimeType mt = getMimeType(url);
        byte[] bytes = downloadResource(url);
        return new TaskResource(bytes, mt);
    }

    public SubmissionResource downloadSubmissionResource(String url) throws Exception {
        MimeType mt = getMimeType(url);
        byte[] bytes = downloadResource(url);
        return new SubmissionResource(bytes, mt);
    }

    public MimeType getMimeType(String link) throws Exception {
        URL url = new URL(link);
        URLConnection conn = url.openConnection();
        String contentType = conn.getContentType();
        MimeType mt;
        if(contentType.equals("application/zip"))
            mt = MimeType.ZIP;
        else if(contentType.equals("application/xml"))
            mt = MimeType.XML;
        else
            throw new GrappaException(String.format("Unsupported mime type '%s' of proforma resource.",
                contentType));
        return mt;
    }

    private byte[] downloadResource(String url) throws Exception {
        URL website = new URL(url);
        byte[] bytes;
        try (InputStream in = website.openStream()) {
            return in.readAllBytes();
        }
    }
}
