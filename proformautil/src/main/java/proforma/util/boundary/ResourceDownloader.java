package proforma.util.boundary;


import proforma.util.div.IOUtils;
import proforma.util.div.Strings;
import proforma.util.div.XmlUtils;
import proforma.util.div.Zip;
import proforma.util.exception.UnexpectedDataException;
import proforma.util.resource.MimeType;
import proforma.util.resource.SubmissionResource;
import proforma.util.resource.TaskResource;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

/**
 * This class handles external-task and external-submission
 * resources. (Refer to the Proforma whitepaper for more information.)
 */
public class ResourceDownloader {


    public static class Resource {
        private String contentType;
        private String contentEncoding;
        private String filename;
        private byte[] content;

        public Resource(String contentType, String contentEncoding, String filename, byte[] content) {
            this.contentType = contentType;
            this.contentEncoding = contentEncoding;
            this.filename = filename;
            this.content = content;
        }

        public Resource() {
        }

        public String getContentType() {
            return contentType;
        }

        public void setContentType(String contentType) {
            this.contentType = contentType;
        }

        public String getContentEncoding() {
            return contentEncoding;
        }

        public void setContentEncoding(String contentEncoding) {
            this.contentEncoding = contentEncoding;
        }

        public String getFilename() {
            return filename;
        }

        public void setFilename(String filename) {
            this.filename = filename;
        }

        public byte[] getContent() {
            return content;
        }

        public void setContent(byte[] content) {
            this.content = content;
        }

        /**
         * @return true, if the Content-Type starts with "text/".
         */
        public boolean isTextContent() {
            if (Strings.isNullOrEmpty(getContentType())) {
                return false;
            } else if (getContentType().startsWith("text/")) {
                return true;
            } else {
                return false;
            }
        }

        public boolean isZipContent() {
            if (Strings.isNullOrEmpty(getContentType())) {
                return Zip.isZip(content);
            }
            if (MimeType.ZIP.equals(MimeType.fromContentType(contentType))) {
                return true;
            }
            return false;
        }

        public boolean isXmlContent() {
            if (Strings.isNullOrEmpty(getContentType())) {
                return XmlUtils.isXml(content);
            }
            if (MimeType.XML.equals(MimeType.fromContentType(contentType))) {
                return true;
            }
            return false;
        }

        public String getFileNameOrDefault() {
            if (Strings.isNullOrEmpty(filename)) {
                String prefix;
                if (isTextContent()) {
                    prefix = "text";
                } else {
                    prefix = "bin";
                }
                return prefix + "file";
            }
            return filename;
        }

        public String getEncodingOrUtf8AsDefault() {
            String enc;
            if (Strings.isNullOrEmpty(getContentEncoding())) {
                enc = StandardCharsets.UTF_8.name();
            } else {
                enc = getContentEncoding();
            }
            return enc;
        }

    }


    public TaskResource downloadTaskResource(String url) throws MalformedURLException, UnexpectedDataException, IOException {
        Resource r = downloadResource(url);
        return new TaskResource(r.getContent(), getMimeType(r.getContentType()));
    }

    public SubmissionResource downloadSubmissionResource(String url) throws MalformedURLException, UnexpectedDataException, IOException {
        Resource r = downloadResource(url);
        return new SubmissionResource(r.getContent(), getMimeType(r.getContentType()));
    }

    private MimeType getMimeType(String contentType) throws MalformedURLException, UnexpectedDataException, IOException {
        MimeType mt = MimeType.fromContentType(contentType);
        if (mt == null)
            throw new UnexpectedDataException(String.format("Unsupported mime type '%s' of proforma resource.",
                contentType));
        return mt;
    }


    public Resource downloadResource(String url) throws MalformedURLException, UnexpectedDataException, IOException {
        URL website = new URL(url);
        URLConnection conn = website.openConnection();
        Resource result = new Resource();
        result.setContentType(conn.getContentType());
        result.setContentEncoding(conn.getContentEncoding());

        String filename;
        // source: https://stackoverflow.com/a/10995501
        String raw = conn.getHeaderField("Content-Disposition");
        // raw = "attachment; filename=abc.jpg"
        if (raw != null && raw.indexOf("=") >= 0) {
            filename = raw.split("=")[1]; //getting value after '='
        } else {
            filename = null;
        }
        result.setFilename(filename);

        try (InputStream in = conn.getInputStream()) {
            result.setContent(IOUtils.toByteArray(in));
        }

        return result;
    }
}
