package de.init.backend.authentication;

import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.lang.NonNull;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import de.init.backend.authentication.config.AuthConfiguration;
import de.init.backend.authentication.error.InvalidPasswordException;
import de.init.backend.authentication.error.UserNotFoundException;
import de.init.backend.authentication.error.UsernameAlreadyExistException;
import de.init.backend.authentication.model.User;
import de.init.backend.authentication.repository.UserRepository;

/**
 * This Service implements the {@link AuthenticationService} interface. This
 * service has to be created as a bean in the config.
 */
@Transactional
public class AuthService<T extends User> implements AuthenticationService<T> {

	protected final PasswordEncoder passwordEncoder;
	protected final AuthConfiguration authConfiguration;
	protected final CustomUserDetailsService<T> userService;

	public AuthService(CustomUserDetailsService<T> userService, PasswordEncoder passwordEncoder,
			AuthConfiguration authConfiguration) {
		this.userService = userService;
		this.passwordEncoder = passwordEncoder;
		this.authConfiguration = authConfiguration;
	}

	@Override
	public T getUser(@NonNull long id) throws UserNotFoundException {
		Optional<T> user = this.getRepository().findById(id);
		return user.orElseThrow(() -> new UsernameNotFoundException("No user found by id: " + id));
	}

	@Override
	public T getUser(@NonNull String username) throws UserNotFoundException {
		Optional<T> user = this.getRepository().findByUsername(username);
		return user.orElseThrow(() -> new UsernameNotFoundException("No user found by username: " + username));

	}

	@Override
	public boolean userExists(@NonNull final String username) {
		return this.getRepository().existsByUsername(username);
	}

	@Override
	public T registerNewUser(@NonNull T user) throws UsernameAlreadyExistException, UsernameNotFoundException {
		if (user.getUsername() == null || user.getUsername().isBlank())
			throw new UsernameNotFoundException("A username is required");
		if (this.userExists(user.getUsername()))
			throw new UsernameAlreadyExistException(
					"There is already an account with the username: " + user.getUsername());

		// set properties for user entity object
		user.setPassword(this.passwordEncoder.encode(user.getPassword()));
		user.setCreated(new Date().getTime());
		user.setRoles(Arrays.asList(this.authConfiguration.getDefaultRole()));

		return this.getRepository().save(user);
	}

	@Override
	public void disableUser(@NonNull final long id) throws UserNotFoundException {
		T user = this.getUser(id);
		// check if user is not already disabled

		if (user.isEnabled()) {
			// update enable properties and timestamp
			user.setEnabled(false);
			user.setLastUpdated(new Date().getTime());
			this.getRepository().save(user);
		}
	}

	@Override
	public void enableUser(@NonNull final long id) throws UserNotFoundException {
		T user = this.getUser(id);

		// update enable properties and timestamp
		user.setEnabled(true);
		user.setLastUpdated(new Date().getTime());
		this.getRepository().save(user);

	}

	@Override
	public void validatePassword(@NonNull final long id, @NonNull final String password)
			throws InvalidPasswordException, UserNotFoundException {
		T user = this.getUser(id);
		if (!this.passwordEncoder.matches(password, user.getPassword())) {
			throw new InvalidPasswordException();
		}
	}

	protected UserRepository<T> getRepository() {
		return this.userService.getRepository();
	}

}