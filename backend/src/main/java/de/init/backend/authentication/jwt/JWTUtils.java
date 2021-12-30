package de.init.backend.authentication.jwt;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;

import de.init.backend.authentication.UserPrinciple;
import de.init.backend.authentication.model.User;

/**
 * Utility class for JWT token. Covers generation and validation of token.
 */

@Component
public class JWTUtils {
	private static final Logger logger = LoggerFactory.getLogger(JWTUtils.class);

	public static final String TOKEN_PREFIX = "Bearer ";
	public static final String HEADER_STRING = HttpHeaders.AUTHORIZATION;
	public static final String REFRESH_TOKEN_HEADER_STRING = "X-Authorization";

	private static String jwtSecret;
	private static long jwtExpirationMs;

	public JWTUtils(@Value("${app.jwt.secret}") String jwtSecret,
			@Value("${app.jwt.expiration_time}") long jwtExpirationMs) {
		JWTUtils.jwtSecret = jwtSecret;
		JWTUtils.jwtExpirationMs = jwtExpirationMs;
	}

	public static String generateToken(Authentication authentication) {
		UserPrinciple userPrincipal = (UserPrinciple) authentication.getPrincipal();
		return createToken(userPrincipal.getUsername());
	}

	public static String generateToken(UserPrinciple user) {
		return createToken(user.getUsername());
	}

	public static String generateToken(User user) {
		return createToken(user.getUsername());
	}

	public static String getUserNameFromToken(String token) {
		return JWT.decode(token).getSubject();
	}

	public static boolean validateToken(String authToken) {
		try {
			Algorithm algorithm = Algorithm.HMAC512(jwtSecret);
			JWTVerifier verifier = JWT.require(algorithm).build();
			verifier.verify(authToken);
			return true;
		} catch (JWTVerificationException e) {
			logger.error("Invalid JWT token: {}", e.getMessage());
		} catch (IllegalArgumentException e) {
			logger.error("JWT claims string is empty: {}", e.getMessage());
		}

		return false;
	}

	private static String createToken(String subject) {
		String token = JWT.create().withSubject(subject)
				.withExpiresAt(new Date(System.currentTimeMillis() + jwtExpirationMs))
				.sign(Algorithm.HMAC512(jwtSecret));

		return token;
	}
}