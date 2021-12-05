package de.hsh.grappa.boundary;


import de.hsh.grappa.common.MimeType;
import de.hsh.grappa.common.SubmissionResource;
import de.hsh.grappa.common.TaskResource;
import de.hsh.grappa.exceptions.GrappaException;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * This class handles external-task and external-submission
 * resources. (Refer to the Proforma whitepaper for more information.)
 */
class ResourceDownloader {
    public TaskResource downloadTaskResource(String url) throws MalformedURLException, IOException, GrappaException {
        MimeType mt = getMimeType(url);
        byte[] bytes = downloadResource(url);
        return new TaskResource(bytes, mt);
    }

    public SubmissionResource downloadSubmissionResource(String url) throws GrappaException, MalformedURLException, IOException {
        MimeType mt = getMimeType(url);
        byte[] bytes = downloadResource(url);
        return new SubmissionResource(bytes, mt);
    }

    private MimeType getMimeType(String link) throws GrappaException, MalformedURLException, IOException {
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

    private byte[] downloadResource(String url) throws MalformedURLException, IOException {
        URL website = new URL(url);
        try (InputStream in = website.openStream()) {
            return in.readAllBytes();
        }
    }
}
