package de.init.backend.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import de.init.backend.model.dto.AccountDto;
import de.init.backend.model.dto.NewAccountDto;
import de.init.backend.model.dto.PasswordDto;
import de.init.backend.services.AccountService;

/**
 * Check auth by user roles for specific route with the @PreAuthorize annotation
 * provided by Spring Security. This annotation can be applied to a class or
 * method, and it accepts a single string value.
 * 
 * Roles and authorities are similar in Spring. The main difference is that,
 * roles have special semantics – starting with Spring Security 4, the ‘ROLE_‘
 * prefix is automatically added (if it's not already there) by any role related
 * method.
 * 
 * So hasAuthority(‘ROLE_ADMIN') is similar to hasRole(‘ADMIN') because the
 * ‘ROLE_‘ prefix gets added automatically. In this application wie use
 * 'hasAuthority', because we have a double layer role system.
 * 
 * In out application we can also use furthermore hasPermission('PERMISSION') to
 * control access by specific permissions.
 * 
 * @source https://www.baeldung.com/spring-security-check-user-role
 * @source https://www.baeldung.com/spring-security-expressions
 *
 */
@Controller
@RequestMapping("${app.endpoint.api}/accounts")
public class AccountController {

	private final AccountService accountService;

	@Autowired
	public AccountController(AccountService accountService) {
		this.accountService = accountService;
	}

	@GetMapping("")
	@PreAuthorize("hasPermission('READ_PERMISSION')")
	public ResponseEntity<?> getAllAccount() {
		List<AccountDto> accounts = this.accountService.getAllAccounts();
		return ResponseEntity.ok(accounts);
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasPermission('READ_PERMISSION')")
	public ResponseEntity<?> getAccountById(@PathVariable long id) {
		AccountDto account = this.accountService.getAccountById(id);
		return ResponseEntity.ok(account);
	}

	@PutMapping("/")
	@PreAuthorize("hasPermission('WRITE_PERMISSION')")
	public ResponseEntity<?> createAccount(@RequestBody @Valid NewAccountDto accountDto) {
		AccountDto account = this.accountService.createAccount(accountDto);
		account.setStatus(HttpStatus.CREATED.value());
		return new ResponseEntity<>(account, HttpStatus.CREATED);
	}

	@PostMapping("/{id}/update")
	@PreAuthorize("hasPermission('WRITE_PERMISSION')")
	public ResponseEntity<?> updateAccount(@PathVariable long id, @RequestBody @Valid AccountDto accountDto) {
		AccountDto account = this.accountService.updateAccount(id, accountDto);
		return ResponseEntity.ok(account);
	}

	@PostMapping("/{id}/password")
	@PreAuthorize("hasPermission('WRITE_PERMISSION')")
	public ResponseEntity<?> changePassword(@PathVariable long id, @RequestBody @Valid PasswordDto passwordDto) {
		AccountDto account = this.accountService.changePassword(id, passwordDto.getNewPassword());
		return ResponseEntity.ok(account);
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasPermission('WRITE_PERMISSION')")
	public ResponseEntity<?> deleteAccount(@PathVariable long id) {
		this.accountService.deleteAccount(id);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@GetMapping("/page")
	@PreAuthorize("hasPermission('READ_PERMISSION')")
	public ResponseEntity<?> getPage(@RequestParam(defaultValue = "3") Integer size,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "id") String sort,
			@RequestParam(defaultValue = "true") Boolean ascending, @RequestParam(required = false) String search,
			@RequestParam(required = false) String filter) {
		Page<AccountDto> pageDto = this.accountService.getPage(page, size, sort, ascending, search, filter);
		return ResponseEntity.ok(pageDto);
	}

	@GetMapping("/create_test")
	@PreAuthorize("hasPermission('WRITE_PERMISSION')")
	public ResponseEntity<?> createTestAccounts() {

		this.accountService.addTestAccounts();
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

}