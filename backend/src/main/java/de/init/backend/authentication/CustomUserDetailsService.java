package de.init.backend.authentication;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import de.init.backend.authentication.error.BlockedException;
import de.init.backend.authentication.model.User;
import de.init.backend.authentication.repository.UserRepository;

/**
 * Generic type extends of User to use inherited user entities
 * 
 * See also:
 * https://www.baeldung.com/role-and-privilege-for-spring-security-registration
 */
@Service
public class CustomUserDetailsService<T extends User> implements UserDetailsService {

	private final UserRepository<T> userRepository;
	private final LoginAttemptService loginAttemptService;
	private final HttpServletRequest request;

	@Autowired
	public CustomUserDetailsService(UserRepository<T> userRepository, LoginAttemptService loginAttemptService,
			HttpServletRequest request) {
		this.userRepository = userRepository;
		this.loginAttemptService = loginAttemptService;
		this.request = request;
	}

	@Override
	@Transactional
	public UserPrinciple loadUserByUsername(String username) throws UsernameNotFoundException {
		// we first need to check if this IP address is blocked
		String ip = this.getClientIP();
		if (loginAttemptService.isBlocked(ip)) {
			throw new BlockedException();
		}

		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

		return new UserPrinciple(user);
	}

	/**
	 * an unsuccessful authentication attempt increases the number of attempts for
	 * that request IP
	 * 
	 */
	public void loginFailed() {
		String ip = this.getClientIP();
		// if ip is not already blocked
		if (!loginAttemptService.isBlocked(ip)) {
			loginAttemptService.loginFailed(ip);
		}
	}

	/**
	 * the successful authentication resets that counter
	 */
	public void loginSucceeded() {
		String ip = this.getClientIP();
		loginAttemptService.loginSucceeded(ip);
	}

	protected UserRepository<T> getRepository() {
		return this.userRepository;
	}

	/**
	 * identify the original IP address of the Client
	 * 
	 * @return IP
	 */
	private String getClientIP() {
		String xfHeader = request.getHeader("X-Forwarded-For");
		if (xfHeader == null) {
			return request.getRemoteAddr();
		}
		return xfHeader.split(",")[0];
	}
}
