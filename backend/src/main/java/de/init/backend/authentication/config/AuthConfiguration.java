package de.init.backend.authentication.config;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import de.init.backend.authentication.model.Role;
import de.init.backend.authentication.repository.RoleRepository;

/**
 * This abstract class has to be implemented outside of the authentication
 * component, set some config.
 *
 */
public abstract class AuthConfiguration {

	private final RoleRepository roleRepository;

	@Autowired
	public AuthConfiguration(RoleRepository roleRepository) {
		this.roleRepository = roleRepository;
	}

	protected Optional<Role> getRole(String roleName) {
		return roleRepository.findByName(roleName);
	}

	public Role getDefaultRole() {
		return this.getRole(this.getDefaultRoleName()).orElse(null);
	}

	/**
	 * Defines the default role for new users
	 * 
	 * @return String
	 */
	public abstract String getDefaultRoleName();

	/**
	 * Defines a map with the role name as key and a collection of privileges as
	 * value
	 * 
	 * @return Map<String role, Collection<String privileges>
	 */
	public abstract Map<String, Collection<String>> getRoles();

	/**
	 * Defines the max amount of login attempts before an user get disabled. -1
	 * deacitvate this feature. Default is 10.
	 */
	public int getMaxLoginAttempts() {
		return 10;
	}
}
