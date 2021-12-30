package de.init.backend;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import de.init.backend.authentication.AuthSessionService;
import de.init.backend.authentication.AuthenticationSessionService;
import de.init.backend.authentication.CustomUserDetailsService;
import de.init.backend.authentication.config.AuthSecurityConfiguration;
import de.init.backend.authentication.repository.UserRepository;
import de.init.backend.model.entity.Account;
import de.init.backend.repositories.AccountRepository;

@Configuration
public class SecurityConfig extends AuthSecurityConfiguration {

	private final String loginEndpoint;
	private final String apiEndpoint;
	private final String registrationEndpoint;

	private final AccountRepository accountRepository;

	public SecurityConfig(UserDetailsService userDetailsService, AccountRepository accountRepository,
			AuthConfig authConfig, @Value("${app.endpoint.api}") String apiEndpoint,
			@Value("${app.endpoint.login}") String loginEndpoint,
			@Value("${app.endpoint.registration}") String registrationEndpoint) {
		super(userDetailsService, authConfig);

		this.apiEndpoint = apiEndpoint;
		this.loginEndpoint = loginEndpoint;
		this.registrationEndpoint = registrationEndpoint;
		this.accountRepository = accountRepository;
	}

	/**
	 * Here the UserRepositroy will be overwritten with the AccountRepository
	 */
	@Bean
	public UserRepository<Account> configureUserRepository() {
		return this.accountRepository;
	}

	/**
	 * Here we create a AuthenticationUserService bean with the type for our user
	 * inherited Account entity
	 */
	@SuppressWarnings("unchecked")
	@Override
	@Bean
	protected AuthenticationSessionService<Account> sessionService() {
		return new AuthSessionService<>((CustomUserDetailsService<Account>) userDetailsService,
				passwordEncoder(), authConfiguration);
	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(@NonNull CorsRegistry registry) {
				// TODO: Prüfen, ob das Mapping eingeschränkt werden sollte
				registry.addMapping("/**").allowedOrigins("*").allowedOrigins("http://localhost:8081")
						.allowedMethods("HEAD", "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
						.allowCredentials(true);
			}
		};
	}

	/** configure authorization routes */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// disable cors and csrf
		http.cors().and().csrf().disable();
		// enable basic auth
		http.httpBasic()
				// set secured endpoints
				.and().authorizeRequests()
				// disable authentication for the login endpoint
				.antMatchers(this.apiEndpoint + this.loginEndpoint).permitAll()
				// disable authentication for the registration route
				.antMatchers(this.apiEndpoint + this.registrationEndpoint).permitAll()
				// disable authentication for all accounts route
				// enable h2 console for all
				.antMatchers(this.apiEndpoint + "/h2-console/**").permitAll()
				// enable authentication for all other endpoints
				.anyRequest().authenticated()
				// this is needed for h2-console
				.and().headers().frameOptions().sameOrigin();// allow use of frame to same origin urls

		// call super configuration
		super.configure(http);
	}

	@Override
	protected String getLoginRoute() {
		return this.apiEndpoint + this.loginEndpoint;
	}
}