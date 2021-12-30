package de.init.backend.authentication;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import de.init.backend.authentication.config.AuthConfiguration;
import de.init.backend.authentication.jwt.JWTAuthenticationHandler;
import de.init.backend.authentication.model.Permission;
import de.init.backend.authentication.model.Role;
import de.init.backend.authentication.repository.PermissionRepository;
import de.init.backend.authentication.repository.RoleRepository;

/**
 * We'll tie this to the startup of the application and we'll use an
 * ApplicationListener on ContextRefreshedEvent to load our initial data on
 * server start. See:
 * https://www.baeldung.com/role-and-privilege-for-spring-security-registration
 *
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class RoleInitialization implements ApplicationListener<ContextRefreshedEvent> {
	private final Logger logger = LoggerFactory.getLogger(JWTAuthenticationHandler.class);

	/**
	 * Note how we're using an alreadySetup flag to determine if the setup needs to
	 * run or not. This is simply because, depending on how many contexts you have
	 * configured in your application – the ContextRefreshedEvent may be fired
	 * multiple times. And we only want the setup to be executed once.
	 */
	boolean alreadySetup = false;

	private final RoleRepository roleRepository;
	private final PermissionRepository permissionRepository;
	private final AuthConfiguration authConfiguration;

	@Autowired
	public RoleInitialization(RoleRepository roleRepository, PermissionRepository permissionRepository,
			AuthConfiguration authConfiguration) {
		this.roleRepository = roleRepository;
		this.permissionRepository = permissionRepository;
		this.authConfiguration = authConfiguration;
	}

	@Override
	@Transactional
	public void onApplicationEvent(ContextRefreshedEvent event) {
		if (alreadySetup)
			return;

		// initialize all roles if not done
		Map<String, Collection<String>> roles = this.authConfiguration.getRoles();

		roles.forEach((role, permissions) -> {
			// creating the privileges
			Collection<Permission> permissionCollection = (Collection<Permission>) permissions.stream()
					.map(this::createPrivilege).collect(Collectors.toList());
			// creating the role and assigning the privileges to them
			this.createRole(role, permissionCollection);
		});
		logger.info("Created roles {}", roles);

		alreadySetup = true;
	}

	private Permission createPrivilege(String name) {
		Optional<Permission> oPermission = permissionRepository.findByName(name);
		if (oPermission.isEmpty()) {
			Permission permission = new Permission(name);
			return permissionRepository.save(permission);
		}
		return oPermission.get();
	}

	private Role createRole(String name, Collection<Permission> permissions) {
		Optional<Role> oRole = this.roleRepository.findByName(name);
		if (oRole.isEmpty()) {
			Role roleObject = new Role(name);
			roleObject.setPrivileges(permissions);
			return roleRepository.save(roleObject);
		}
		return oRole.get();
	}

}