package de.init.backend.controller;

import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import de.init.backend.authentication.AuthenticationSessionService;
import de.init.backend.authentication.error.UserNotEnabledException;
import de.init.backend.authentication.error.UsernameAlreadyExistException;
import de.init.backend.authentication.jwt.JWTUtils;
import de.init.backend.model.dto.DetailedAccountDto;
import de.init.backend.model.dto.PasswordDto;
import de.init.backend.model.entity.Account;

/**
 * Controller with functions for current authenticated user. All endpoints are
 * available for every logged in User.
 * 
 */
@Controller
@RequestMapping("${app.endpoint.api}/user")
public class UserController {

	AuthenticationSessionService<Account> userService;

	@Autowired
	public UserController(AuthenticationSessionService<Account> userService) {
		this.userService = userService;
	}

	@GetMapping("")
	public ResponseEntity<?> getCurrentUser() {
		Account user = this.userService.getAuthenticatedUser();
		return ResponseEntity.ok(new DetailedAccountDto(user));
	}

	@GetMapping("/check")
	public ResponseEntity<?> checkUsernameExists(@RequestParam String username) {
		Boolean exists = this.userService.userExists(username);
		return ResponseEntity.ok(exists);
	}

	@PostMapping("/update/username")
	public ResponseEntity<?> updateUsername(HttpServletResponse response, @RequestBody String username)
			throws UserNotEnabledException, UsernameAlreadyExistException, LoginException {
		// change username
		Account user = this.userService.changeUsername(username);

		// update refresh token with new username
		String refreshedToken = JWTUtils.generateToken(user);
		response.setHeader(JWTUtils.REFRESH_TOKEN_HEADER_STRING,
				String.format("%s%s", JWTUtils.TOKEN_PREFIX, refreshedToken));

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping("/update/password")
	public ResponseEntity<?> updatePassword(@RequestBody @Valid PasswordDto passwordDto)
			throws UserNotEnabledException, LoginException {
		this.userService.changePassword(passwordDto.getOldPassword(), passwordDto.getNewPassword());
		return new ResponseEntity<>(HttpStatus.OK);
	}

}