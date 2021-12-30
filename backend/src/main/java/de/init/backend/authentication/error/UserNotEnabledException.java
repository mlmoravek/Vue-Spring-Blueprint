package de.init.backend.authentication.error;


public final class UserNotEnabledException extends RuntimeException {

    private static final long serialVersionUID = 5861310537366287163L;

    public UserNotEnabledException() {
        super("User not enabled");
    }

    public UserNotEnabledException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public UserNotEnabledException(final String message) {
        super(message);
    }

    public UserNotEnabledException(final Throwable cause) {
        super(cause);
    }

}