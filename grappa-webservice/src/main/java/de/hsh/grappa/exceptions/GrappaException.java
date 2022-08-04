
package de.hsh.grappa.exceptions;

/**
 * GrappaException basically indicates server errors
 */
public class GrappaException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = -8330435536570550002L;

    /**
     *
     */
    public GrappaException() {

    }

    /**
     * @param message
     */
    public GrappaException(String message) {
        super(message);

    }

    /**
     * @param cause
     */
    public GrappaException(Throwable cause) {
        super(cause);

    }

    /**
     * @param message
     * @param cause
     */
    public GrappaException(String message, Throwable cause) {
        super(message, cause);

    }

    /**
     * @param message
     * @param cause
     * @param enableSuppression
     * @param writableStackTrace
     */
    public GrappaException(String message, Throwable cause,
                           boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);

    }

}
