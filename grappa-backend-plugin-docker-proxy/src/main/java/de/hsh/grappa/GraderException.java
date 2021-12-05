package de.hsh.grappa;

public class GraderException extends Exception {
    
	private static final long serialVersionUID = 4581470094516588765L;

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

