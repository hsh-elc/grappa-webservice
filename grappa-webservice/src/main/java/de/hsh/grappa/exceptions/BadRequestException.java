package de.hsh.grappa.exceptions;

/***
 * For any type of invalid data that is not otherwise covered by a more specific
 * exception type. Example: Ill-formatted submissions (mimetype's not set/no
 * content), invalid object Ids, ...
 *
 */
public class BadRequestException extends Exception { // must not be GrappaException {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    /**
	 * 
	 */
    public BadRequestException() {

    }

    /**
     * @param s
     */
    public BadRequestException(String s) {
        super(s);
    }

    /***
     * 
     * @param message
     * @param cause
     */
    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public BadRequestException(Throwable cause) {
        super(cause);
    }
}
