package proforma.util.exception;

public class UnexpectedDataException extends Exception {

    private static final long serialVersionUID = 57937972127716009L;

    public UnexpectedDataException() {
    }

    public UnexpectedDataException(String s) {
        super(s);
    }

    public UnexpectedDataException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnexpectedDataException(Throwable cause) {
        super(cause);
    }
}
