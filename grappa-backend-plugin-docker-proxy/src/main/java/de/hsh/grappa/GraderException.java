package de.hsh.grappa;

public class GraderException extends Exception {
    public GraderException() {
    }

    public GraderException(String message) {
        super(message);
    }

    public GraderException(Throwable cause) {
        super(cause);
    }

    public GraderException(String message, Throwable cause) {
        super(message, cause);
    }
}

