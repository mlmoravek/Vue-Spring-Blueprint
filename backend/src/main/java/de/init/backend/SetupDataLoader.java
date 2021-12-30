package de.init.backend;

import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import de.init.backend.AuthConfig.ROLE;
import de.init.backend.authentication.model.Role;
import de.init.backend.authentication.repository.RoleRepository;
import de.init.backend.model.entity.Account;
import de.init.backend.repositories.AccountRepository;

@Component
class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {
	private final Logger logger = LoggerFactory.getLogger(SetupDataLoader.class);

	private final AccountRepository accountRepository;
	private final RoleRepository roleRepository;
	private final AuthConfig authConfiguration;
	private final PasswordEncoder passwordEncoder;

	private final boolean isProd;

	@Autowired
	public SetupDataLoader(AccountRepository accountRepository, 
			RoleRepository roleRepository, AuthConfig authConfiguration,
			@Value("${app.environment}") String environment, PasswordEncoder passwordEncoder) {
		this.roleRepository = roleRepository;
		this.authConfiguration = authConfiguration;
		this.accountRepository = accountRepository;
		this.passwordEncoder = passwordEncoder;

		this.isProd = "production".equalsIgnoreCase(environment) || "prod".equalsIgnoreCase(environment);;
	}

	@Override
	@Transactional
	public void onApplicationEvent(@NonNull ContextRefreshedEvent event) {
		// creating a admin user and assigning a role to it if the user is not already
		// created
		if (!isProd && accountRepository.findByUsername("admin").isEmpty()) {
			
			// get admin role for admin account
			String adminRoleName = ROLE.ROLE_ADMIN.toString();
			Optional<Role> adminRole = roleRepository.findByName(adminRoleName);
			// get default role for admin account
			String defaultRoleName = this.authConfiguration.getDefaultRoleName();
			Optional<Role> defaultRole = roleRepository.findByName(defaultRoleName);
			
			// create admin account
			Account user = new Account();
			user.setPassword(passwordEncoder.encode("admin"));
			user.setUsername("admin");
			user.setRoles(Arrays.asList(adminRole.orElseThrow(), defaultRole.orElseThrow()));
			user.setCreated(new Date().getTime());
			user.setEnabled(true);

			// create admin account 
			Account acc = this.accountRepository.save(user);

			logger.info("Create Admin Account: {}", acc);
		}
	}
}