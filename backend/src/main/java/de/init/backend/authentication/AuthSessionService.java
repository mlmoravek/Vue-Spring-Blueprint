package de.init.backend.authentication;

import java.util.Date;

import javax.security.auth.login.LoginException;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import de.init.backend.authentication.config.AuthConfiguration;
import de.init.backend.authentication.error.InvalidPasswordException;
import de.init.backend.authentication.error.UserNotEnabledException;
import de.init.backend.authentication.error.UsernameAlreadyExistException;
import de.init.backend.authentication.model.User;

/**
 * This Service implements the {@link AuthenticationSessionService} interface. This
 * service has to be created as a bean in the config.
 */
@Transactional
public class AuthSessionService<T extends User> extends AuthService<T> implements AuthenticationSessionService<T> {

	public AuthSessionService(CustomUserDetailsService<T> userService, PasswordEncoder passwordEncoder,
			AuthConfiguration authConfiguration) {
		super(userService, passwordEncoder, authConfiguration);
	}

	@Override
	public void loginFailed() {
		this.userService.loginFailed();
	}

	@Override
	public void loginSucceeded() {
		this.userService.loginSucceeded();
	}

	@Override
	public T signIn() throws LoginException, UserNotEnabledException {
		this.checkIsLoggedIn();
		this.checkIsEnabled();

		// update timestamp
		T user = this.getAuthenticatedUser();
		user.setLastLogin(new Date().getTime());
		return this.getRepository().save(user);
	}

	@Override
	public T getAuthenticatedUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
			UserDetails principal = (UserDetails) authentication.getPrincipal();
			if (principal == null)
				return null;
			String username = principal.getUsername();
			T user = this.getRepository().findByUsername(username).orElse(null);
			return user;
		} else
			return null;
	}

	@Override
	public T changePassword(@NonNull String oldPassword, @NonNull final String password)
			throws UserNotEnabledException, LoginException {
		this.checkIsLoggedIn();
		this.checkIsEnabled();
		// validate old password
		this.validatePassword(oldPassword);

		// update password and updated timestamp
		T dbUser = this.getAuthenticatedUser();
		dbUser.setPassword(this.passwordEncoder.encode(password));
		dbUser.setLastUpdated(new Date().getTime());
		return this.getRepository().save(dbUser);
	}

	@Override
	public T changeUsername(@NonNull final String username)
			throws UserNotEnabledException, UsernameAlreadyExistException, LoginException {
		this.checkIsLoggedIn();
		this.checkIsEnabled();

		T dbUser = this.getAuthenticatedUser();
		if (username.isBlank())
			throw new UsernameNotFoundException("A username is required");
		if (dbUser.getUsername().equals(username))
			// username is the same - do nothing
			return dbUser;
		if (this.userExists(username))
			throw new UsernameAlreadyExistException("There is already an account with the username: " + username);

		// update user without password and change updated timstamp
		dbUser.setUsername(username);
		dbUser.setLastUpdated(new Date().getTime());
		return this.getRepository().save(dbUser);
	}

	@Override
	public void disableUser() throws LoginException {
		this.checkIsLoggedIn();

		// check if user is not already disabled
		if (this.isUserEnabled()) {
			// update enable properties and timestamp
			T user = this.getAuthenticatedUser();
			user.setEnabled(false);
			user.setLastUpdated(new Date().getTime());
			this.getRepository().save(user);
		}
	}

	@Override
	public void validatePassword(final String password) throws InvalidPasswordException {
		if (!this.passwordEncoder.matches(password, this.getAuthenticatedUser().getPassword())) {
			throw new InvalidPasswordException();
		}
	}

	/**
	 * Check if an user is logged in
	 * 
	 * @throws LoginException
	 */
	private void checkIsLoggedIn() throws LoginException {
		User user = this.getAuthenticatedUser();
		if (user == null)
			throw new LoginException();
	}

	/**
	 * Check if the current user is enabled
	 * 
	 * @throws UserNotEnabledException
	 */
	private void checkIsEnabled() throws UserNotEnabledException {
		if (!this.isUserEnabled())
			throw new UserNotEnabledException();
	}

	private boolean isUserEnabled() {
		User user = this.getAuthenticatedUser();
		if (user == null)
			return false;
		return user.isEnabled();
	}

}