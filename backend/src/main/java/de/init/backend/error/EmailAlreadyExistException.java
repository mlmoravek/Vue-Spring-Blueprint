package de.init.backend.error;

public class EmailAlreadyExistException extends RuntimeException {

    private static final long serialVersionUID = 5861879537366287163L;

    public EmailAlreadyExistException() {
        super("The exist already an account with this email");
    }

    public EmailAlreadyExistException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public EmailAlreadyExistException(final String message) {
        super(message);
    }

    public EmailAlreadyExistException(final Throwable cause) {
        super(cause);
    }

}