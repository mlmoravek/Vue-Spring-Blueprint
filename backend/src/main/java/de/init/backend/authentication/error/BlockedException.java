package de.init.backend.authentication.error;


public final class BlockedException extends RuntimeException {

    private static final long serialVersionUID = 5861310537366287163L;

    public BlockedException() {
        super("Blocked");
    }

    public BlockedException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public BlockedException(final String message) {
        super(message);
    }

    public BlockedException(final Throwable cause) {
        super(cause);
    }

}