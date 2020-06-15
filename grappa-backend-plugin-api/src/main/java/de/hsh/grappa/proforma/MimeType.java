package de.hsh.grappa.proforma;

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
}
