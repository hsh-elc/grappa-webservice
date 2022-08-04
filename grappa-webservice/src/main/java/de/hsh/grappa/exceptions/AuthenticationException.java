package de.hsh.grappa.exceptions;

public class AuthenticationException extends RuntimeException {
    /**
     *
     */
    private static final long serialVersionUID = -8130904669799125215L;

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(Throwable cause) {
        super(cause);
    }
}