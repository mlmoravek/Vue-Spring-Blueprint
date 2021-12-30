package de.init.backend.authentication.jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.util.StringUtils;

/**
 * Filters requests without JWT-Token for authenticated routes
 * 
 * @source https://bezkoder.com/spring-boot-jwt-authentication/
 */
public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

	private final Logger logger = LoggerFactory.getLogger(JWTAuthorizationFilter.class);

	private final UserDetailsService userDetailsService;

	public JWTAuthorizationFilter(AuthenticationManager authManager, UserDetailsService userDetailsService) {
		super(authManager);
		this.userDetailsService = userDetailsService;
	}

	/**
	 * Validates JWT-Token and refreshes Token as long as user is active. --
	 * {@inheritDoc}
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		try {
			// get token from header
			String token = this.getTokenFromHeader(request);
			// check if no user is currently authenticated
			// if the request has JWT, validate it, parse username from it
			if (token != null && SecurityContextHolder.getContext().getAuthentication() == null
					&& JWTUtils.validateToken(token)) {

				// get username from token
				String username = JWTUtils.getUserNameFromToken(token);
				// from username, get UserDetails to create an Authentication object
				UserDetails userDetails = userDetailsService.loadUserByUsername(username);

				// create authentication
				UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
						userDetails, null, userDetails.getAuthorities());
				authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				// set the current UserDetails in SecurityContext using
				// setAuthentication(authentication) method
				SecurityContextHolder.getContext().setAuthentication(authenticationToken);

				// generate new token
				String refreshedToken = JWTUtils.generateToken(authenticationToken);

				// allow web client to read custom header
				response.addHeader("Access-Control-Expose-Headers", JWTUtils.REFRESH_TOKEN_HEADER_STRING);
				// add token to header
				response.setHeader(JWTUtils.REFRESH_TOKEN_HEADER_STRING,
						String.format("%s%s", JWTUtils.TOKEN_PREFIX, refreshedToken));
			}
		} catch (Exception e) {
			SecurityContextHolder.clearContext();
			logger.error("Cannot set user authentication: {}", e);
		}

		// call next filter in chain
		filterChain.doFilter(request, response);
	}

	/**
	 * get JWT from the Authorization header (by removing Bearer prefix)
	 * 
	 * @param request
	 * @return
	 */
	private String getTokenFromHeader(HttpServletRequest request) {
		String tokenHeader = request.getHeader(JWTUtils.HEADER_STRING);

		if (StringUtils.hasText(tokenHeader) && tokenHeader.startsWith(JWTUtils.TOKEN_PREFIX)) {
			return tokenHeader.substring(JWTUtils.TOKEN_PREFIX.length());
		}

		return null;
	}
}
