package de.init.backend.authentication.jwt;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.security.auth.login.LoginException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import de.init.backend.authentication.AuthenticationSessionService;
import de.init.backend.authentication.UserPrinciple;
import de.init.backend.authentication.error.UserNotEnabledException;
import de.init.backend.authentication.model.ErrorResponseDto;
import de.init.backend.authentication.model.User;
import de.init.backend.authentication.model.LoginResponseDto;

/**
 * This class extends UsernamePasswordAuthenticationFilter which is the default
 * class for password authentication in Spring Security. We extend it to define
 * our custom authentication logic.
 * 
 * @source https://www.freecodecamp.org/news/how-to-setup-jwt-authorization-and-authentication-in-spring/
 *
 */
public class JWTAuthenticationHandler extends UsernamePasswordAuthenticationFilter
		implements AuthenticationFailureHandler, AuthenticationSuccessHandler {

	private final Logger logger = LoggerFactory.getLogger(JWTAuthenticationHandler.class);

	private final AuthenticationSessionService<? extends User> userService;
	private final String loginUrl;

	public JWTAuthenticationHandler(AuthenticationManager authManager, AuthenticationSessionService<? extends User> userService,
			String loginUrl) {
		super(authManager);
		this.userService = userService;
		this.loginUrl = loginUrl;

		// Sets the parameter name which will be used to obtain the username from the
		// login request
		this.setUsernameParameter("username");
		// Sets the parameter name which will be used to obtain the password from the
		// login request
		this.setPasswordParameter("password");

		// We make a call to the setFilterProcessesUrl method in our constructor. This
		// method sets the default login URL to the provided parameter.
		// If you remove this line, Spring Security creates the “/login” endpoint by
		// default. It defines the login endpoint for us, which is why we will not
		// define a login endpoint in our controller explicitly.
		this.setFilterProcessesUrl(loginUrl);

		// set this as authenticationFailureHandler that the onAuthenticationFailure
		// function is called to modify response object
		this.setAuthenticationFailureHandler(this);

		// set this as setAuthenticationSuccessHandler that the onAuthenticationSuccess
		// function is called to modify response object
		this.setAuthenticationSuccessHandler(this);
	}

	/**
	 * The attemptAuthentication function runs when the user tries to log in to our
	 * application. It reads the credentials, creates a user POJO from them, and
	 * then checks the credentials to authenticate.
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		logger.info("Login attemption: {}", this.obtainUsername(request));

		// authenticate
		Authentication authentication = super.attemptAuthentication(request, response);
		SecurityContextHolder.getContext().setAuthentication(authentication);

		try {
			// call service that user try to log in
			this.userService.signIn();
		} catch (UserNotEnabledException e) {
			throw new AuthenticationServiceException("User is disabled", e);
		} catch (LoginException e) {
			throw new AuthenticationServiceException("Authorized failed", e);
		}

		return authentication;
	}

	/**
	 * If the authentication is successful, the successfulAuthentication method
	 * runs. The parameters of this method are passed by Spring Security behind the
	 * scenes.
	 *
	 * {@inheritDoc}
	 */
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
		UserPrinciple userPrinciple = (UserPrinciple) authResult.getPrincipal();
		logger.info("User authenticated: {}", userPrinciple.getUsername());

		// call service that login succeeded
		this.userService.loginSucceeded();

		super.successfulAuthentication(request, response, chain, authResult);
	}

	/**
	 * Is called after a successful authentication. This function creates the
	 * response body for a successful authentication.
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		// create token
		User user = this.userService.getAuthenticatedUser();
		String token = JWTUtils.generateToken(user);
		// create UserDto
		LoginResponseDto responseObject = new LoginResponseDto(HttpServletResponse.SC_OK, user, token);

		// add token to header
		response.addHeader(JWTUtils.HEADER_STRING, JWTUtils.TOKEN_PREFIX + token);
		// set response status
		response.setStatus(HttpServletResponse.SC_OK);
		// set response body
		this.writeBody(response, responseObject);
	}

	/**
	 * Fallback point, when Authentication fails.
	 * 
	 * {@inheritDoc}
	 */
	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException failed) throws IOException, ServletException {
		logger.error("Unauthorized error: {}", failed.getMessage());

		// call service that login failed
		this.userService.loginFailed();

		super.unsuccessfulAuthentication(request, response, failed);
	}

	/**
	 * Is called after a failed authentication try. This function creates the
	 * response body for a failed authentication. {@inheritDoc}
	 */
	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {

		// write response body
		ErrorResponseDto responseBody = new ErrorResponseDto(HttpServletResponse.SC_UNAUTHORIZED,
				exception.getMessage(), "Authentication Exception", this.loginUrl);
		this.writeBody(response, responseBody);
		// set unauthorized status
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	}

	/**
	 * Write the object as json into the response body and set the application/json
	 * header
	 * 
	 * @param response HttpServletResponse
	 * @param body     Response Object
	 * @throws IOException
	 */
	private void writeBody(HttpServletResponse response, Object body) throws IOException {
		// create UserDto as JSON
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		String json = ow.writeValueAsString(body);

		// set response body
		response.addHeader(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");
		response.getOutputStream().write(json.getBytes(StandardCharsets.UTF_8));
	}

}