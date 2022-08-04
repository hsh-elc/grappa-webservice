package proforma.util.resource;

/**
 * This type indicates two different Proforma resources types,
 * ZIP and XML.
 */
public enum MimeType {
    XML("text/xml"),
    ZIP("application/zip");

    private String name;

    MimeType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name.toString();
    }

    public static MimeType fromContentType(String s) {
        switch (s) {
            case "text/xml":
            case "application/xml":
                return XML;
            case "application/zip":
            case "application/x-zip":
            case "application/x-zip-compressed":
            case "application/x-compressed":
            case "multipart/x-zip":
                return ZIP;
        }
        return null;
    }

}
