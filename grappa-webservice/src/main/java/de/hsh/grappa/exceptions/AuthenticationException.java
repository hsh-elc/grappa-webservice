package de.hsh.grappa.exceptions;

public class AuthenticationException extends RuntimeException {
    public AuthenticationException(String message, String realm) {
        super(message);
        this.realm = realm;
    }

    public AuthenticationException(Throwable cause) {
        super(cause);
    }

    private String realm = null;

    public String getRealm() {
        return this.realm;
    }
}