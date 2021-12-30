package de.init.backend.authentication.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import de.init.backend.authentication.AuthService;
import de.init.backend.authentication.AuthSessionService;
import de.init.backend.authentication.AuthenticationService;
import de.init.backend.authentication.AuthenticationSessionService;
import de.init.backend.authentication.CustomUserDetailsService;
import de.init.backend.authentication.jwt.JWTAuthenticationHandler;
import de.init.backend.authentication.jwt.JWTAuthorizationFilter;
import de.init.backend.authentication.model.User;

@EnableWebSecurity
public abstract class AuthSecurityConfiguration extends WebSecurityConfigurerAdapter {

	protected final UserDetailsService userDetailsService;
	protected final AuthConfiguration authConfiguration;

	public AuthSecurityConfiguration(UserDetailsService userDetailsService, AuthConfiguration authConfiguration) {
		this.userDetailsService = userDetailsService;
		this.authConfiguration = authConfiguration;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	@Bean
	protected AuthenticationManager authenticationManager() throws Exception {
		return super.authenticationManager();
	}

	@SuppressWarnings("unchecked")
	@Bean
	protected AuthenticationSessionService<? extends User> sessionService() throws Exception {
		return new AuthSessionService<User>((CustomUserDetailsService<User>) userDetailsService, passwordEncoder(),authConfiguration);
	}
	

	@SuppressWarnings("unchecked")
	@Bean
	protected AuthenticationService<? extends User> authenticationService() throws Exception {
		return new AuthService<User>((CustomUserDetailsService<User>) userDetailsService, passwordEncoder(),
				authConfiguration);
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
		super.configure(auth);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// and JWT configuration
		http
				// add authentication handler for every request as filter
				.addFilter(new JWTAuthenticationHandler(this.authenticationManager(), this.sessionService(),
						this.getLoginRoute()))
				// add authorization filter for login request
				.addFilter(new JWTAuthorizationFilter(this.authenticationManager(), this.userDetailsService))
				// this disables session creation on Spring Security
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
	}

	protected abstract String getLoginRoute();
}