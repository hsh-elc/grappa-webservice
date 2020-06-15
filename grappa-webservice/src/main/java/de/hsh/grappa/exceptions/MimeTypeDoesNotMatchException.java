package de.hsh.grappa.exceptions;

public class MimeTypeDoesNotMatchException extends GrappaException {

    public MimeTypeDoesNotMatchException() {
    }

    public MimeTypeDoesNotMatchException(String s) {
        super(s);
    }

    public MimeTypeDoesNotMatchException(Throwable cause) {
        super(cause);
    }

}
