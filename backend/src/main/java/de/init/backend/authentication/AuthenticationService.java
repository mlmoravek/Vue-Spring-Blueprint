package de.init.backend.authentication;

import javax.security.auth.login.LoginException;

import org.springframework.lang.NonNull;

import de.init.backend.authentication.error.InvalidPasswordException;
import de.init.backend.authentication.error.UserNotFoundException;
import de.init.backend.authentication.error.UsernameAlreadyExistException;
import de.init.backend.authentication.model.User;

/**
 * This Service provides some overarching functionality for the authentication
 * component.
 * 
 * Define the response type T dynamically, so the User Entity can be
 * inheritated.
 */
public interface AuthenticationService<T extends User> {

	/**
	 * Returns a user by the given id
	 * 
	 * @param id user id
	 * @return T
	 * @throws UserNotFoundException if no user is found by id
	 */
	T getUser(@NonNull long id) throws UserNotFoundException;

	/**
	 * Returns a user by the given username
	 * 
	 * @param username user username
	 * @return T
	 * @throws UserNotFoundException if no user is found by username
	 */
	T getUser(@NonNull String username) throws UserNotFoundException;

	/**
	 * Check if an user by username already exist
	 */
	boolean userExists(@NonNull final String username);

	/**
	 * Saves a new user in the system
	 * 
	 * @param user
	 * @return T user
	 * @throws UsernameAlreadyExistException if an user with the username already
	 *                                       exist
	 */
	T registerNewUser(@NonNull T user) throws UsernameAlreadyExistException;

	/**
	 * Disable an user by id
	 * 
	 * @throws LoginException
	 * @throws UserNotFoundException
	 */
	void disableUser(@NonNull final long id) throws UserNotFoundException;

	/**
	 * Enable an user by id
	 * 
	 * @param id Id of an existing user
	 * @throws LoginException
	 * @throws UserNotFoundException
	 */
	void enableUser(@NonNull final long id) throws UserNotFoundException;

	/**
	 * Validate a password for the user by id
	 * 
	 * @param id       user id
	 * @param password password to validate
	 * @throws InvalidPasswordException if validation failes
	 */
	void validatePassword(@NonNull final long id, @NonNull final String password)
			throws UserNotFoundException, InvalidPasswordException;

}