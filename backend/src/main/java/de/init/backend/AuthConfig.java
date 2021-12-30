package de.init.backend;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import de.init.backend.authentication.config.AuthConfiguration;
import de.init.backend.authentication.repository.RoleRepository;

/**
 * In this class the User roles will be defined.
 */
@Service
public class AuthConfig extends AuthConfiguration {

	public enum PERMISSION {
		READ_PERMISSION, WRITE_PERMISSION
	}

	public enum ROLE {
		ROLE_ADMIN, ROLE_USER
	}

	private final int maxLoginAttempts;

	@Autowired
	public AuthConfig(RoleRepository roleRepository, @Value("${app.max-login-attempts}") int maxLoginAttempts) {
		super(roleRepository);
		this.maxLoginAttempts = maxLoginAttempts;
	}

	/**
	 * Here the roles and permission are defined
	 */
	@Override
	public Map<String, Collection<String>> getRoles() {
		Map<String, Collection<String>> roleMap = new HashMap<>();

		// add admin role
		roleMap.put(ROLE.ROLE_ADMIN.toString(),
				Arrays.asList(PERMISSION.READ_PERMISSION.toString(), PERMISSION.WRITE_PERMISSION.toString()));
		// add default role
		roleMap.put(ROLE.ROLE_USER.toString(), Arrays.asList(PERMISSION.READ_PERMISSION.toString()));

		return roleMap;
	}

	@Override
	public String getDefaultRoleName() {
		return ROLE.ROLE_USER.toString();
	}

	@Override
	public int getMaxLoginAttempts() {
		return this.maxLoginAttempts;
	}

}
