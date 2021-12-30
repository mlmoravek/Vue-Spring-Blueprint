package de.init.backend.controller;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.init.backend.authentication.jwt.JWTUtils;
import de.init.backend.model.dto.AccountDto;
import de.init.backend.model.dto.NewAccountDto;
import de.init.backend.services.AccountService;

@RestController
@RequestMapping("${app.endpoint.api}")
public class AuthController {
	
	private final AccountService accountService;

	@Autowired
	public AuthController(AccountService accountService) {
		this.accountService = accountService;
	}

	@PutMapping("${app.endpoint.registration}")
	@PreAuthorize("isAnonymous and isAuthenticated")
	public ResponseEntity<?> registerUser(HttpServletResponse response, @RequestBody @Valid NewAccountDto accountDto) {
			AccountDto account = this.accountService.createAccount(accountDto);
			// set auth token with new username
			String token = JWTUtils.generateToken(account.toAccount());
			response.setHeader(JWTUtils.REFRESH_TOKEN_HEADER_STRING,
					String.format("%s%s", JWTUtils.TOKEN_PREFIX, token));
			
			account.setStatus(HttpStatus.CREATED.value());
			return new ResponseEntity<>(account, HttpStatus.CREATED);
	}

	/**
	 * API Endpoint to ping for refreshed Token, because refreshed Token will be in
	 * a Header field on success.
	 */
	@GetMapping("/refresh")
	@PreAuthorize("isAuthenticated")
	public void getRefreshedToken() {
		// nothing needs to be done here, because token will get refreshed in custom
		// servlet filter
	}

}
