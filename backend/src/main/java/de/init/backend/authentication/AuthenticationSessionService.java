package de.init.backend.authentication;

import javax.security.auth.login.LoginException;

import org.springframework.lang.NonNull;

import de.init.backend.authentication.error.InvalidPasswordException;
import de.init.backend.authentication.error.UserNotEnabledException;
import de.init.backend.authentication.error.UserNotFoundException;
import de.init.backend.authentication.error.UsernameAlreadyExistException;
import de.init.backend.authentication.model.User;
import javassist.NotFoundException;

/**
 * This service extends the {@link AuthenticationService} and defines some
 * specific functionality for the current authenticated user.
 * 
 * Define the response type T dynamically, so the User Entity can be inheritated
 */
public interface AuthenticationSessionService<T extends User> extends AuthenticationService<T> {

	/**
	 * Returns the current authenticated user or null if no user is authenticated
	 * 
	 * @return T user
	 */
	T getAuthenticatedUser();

	/**
	 * Check if the current authenticated user and update the login state.
	 * 
	 * @return T current authenticated user
	 * @throws LoginException          no user is logged in
	 * @throws UserNotEnabledException User is not enabled
	 */
	T signIn() throws LoginException, UserNotEnabledException;

	/**
	 * Updated the password from current authenticated user.
	 * 
	 * @param oldPassword password property
	 * @param newPassword password property
	 * @return updated User object
	 * @throws LoginException
	 * @throws NotFoundException if user by id not exist
	 */
	T changePassword(@NonNull String oldPassword, @NonNull final String password)
			throws UserNotEnabledException, LoginException;

	/**
	 * Updates the username from current authenticated user.
	 * 
	 * @param username Username property
	 * @return updated User object
	 * @throws LoginException
	 * @throws NotFoundException if user by id not exist
	 */
	T changeUsername(@NonNull String username)
			throws UserNotEnabledException, UsernameAlreadyExistException, LoginException;

	/**
	 * Validate a password for the current authenticated user
	 * 
	 * @param password password to validate
	 * @throws InvalidPasswordException if validation failes
	 */
	void validatePassword(final String password) throws InvalidPasswordException;

	/**
	 * Adds an login attempt to the user. Disable user if the attempts are more then
	 * the limit and throws UserNotEnabledException.
	 * 
	 * @throws LoginException
	 */
	void loginFailed();

	/**
	 * Reset the login attempts.
	 */
	void loginSucceeded();

	/**
	 * Disable the current authenticated user
	 * 
	 * @throws LoginException
	 * @throws UserNotFoundException
	 */
	void disableUser() throws LoginException;

}