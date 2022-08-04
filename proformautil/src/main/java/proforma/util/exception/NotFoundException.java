package proforma.util.exception;

public class NotFoundException extends Exception {
    /**
     *
     */
    private static final long serialVersionUID = -9065686485673860733L;

    public NotFoundException() {
    }

    public NotFoundException(String s) {
        super(s);
    }

    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotFoundException(Throwable cause) {
        super(cause);
    }
}
