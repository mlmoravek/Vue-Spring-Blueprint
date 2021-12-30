package de.init.backend.authentication.error;

/**
 * @source https://github.com/Baeldung/spring-security-registration/blob/master/src/main/java/com/baeldung/web/error/InvalidOldPasswordException.java
 */
public final class InvalidPasswordException extends RuntimeException {

    private static final long serialVersionUID = 5861310537366287163L;

    public InvalidPasswordException() {
        super("The password is invalid");
    }

    public InvalidPasswordException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public InvalidPasswordException(final String message) {
        super(message);
    }

    public InvalidPasswordException(final Throwable cause) {
        super(cause);
    }

}
