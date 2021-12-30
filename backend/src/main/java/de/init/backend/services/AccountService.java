package de.init.backend.services;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;

import de.init.backend.AuthConfig.ROLE;
import de.init.backend.authentication.AuthenticationSessionService;
import de.init.backend.authentication.error.UserNotFoundException;
import de.init.backend.authentication.error.UsernameAlreadyExistException;
import de.init.backend.datatable.CrudService;
import de.init.backend.datatable.PageService;
import de.init.backend.datatable.annotation.AnnotationService;
import de.init.backend.error.EmailAlreadyExistException;
import de.init.backend.model.dto.AccountDto;
import de.init.backend.model.dto.DetailedAccountDto;
import de.init.backend.model.dto.NewAccountDto;
import de.init.backend.model.entity.Account;
import de.init.backend.repositories.AccountRepository;

@Service
public class AccountService {
	private static final Logger logger = LoggerFactory.getLogger(AccountService.class);

	private final AccountRepository repository;
	private final AuthenticationSessionService<Account> userService;
	private final PasswordEncoder passwordEncoder;
	private final PageService<Account, Long> pageService;
	private final CrudService<Account, Long> crudService;

	@Autowired
	public AccountService(AccountRepository repository, AuthenticationSessionService<Account> userService,
			PasswordEncoder passwordEncoder) {
		this.repository = repository;
		this.userService = userService;
		this.pageService = new PageService<>(repository, Account.class);
		this.crudService = new CrudService<>(repository, Account.class);

		this.passwordEncoder = passwordEncoder;
	}

	public AccountDto getAccountById(long id) {
		Account account = this.crudService.get(id);
		return this.castAccount(account);
	}

	public List<AccountDto> getAllAccounts() {
		List<Account> list = Lists.newArrayList(this.crudService.getAll());
		return list.stream().map(this::castAccount).collect(Collectors.toList());
	}

	public AccountDto createAccount(NewAccountDto accountDto) throws UsernameAlreadyExistException {
		Account account = accountDto.toAccount();

		// check if email already exist
		this.checkEmailExists(account.getEmail());

		account = this.userService.registerNewUser(account);
		logger.info("Created account " + account);
		return this.castAccount(account);
	}

	public AccountDto updateAccount(long id, AccountDto accountDto) throws UserNotFoundException {
		Account account = crudService.get(id);
		if (account == null)
			throw new UserNotFoundException();

		// if username change, check if username already exist
		if (account.getUsername() != null && !account.getUsername().equals(accountDto.getUsername()))
			this.checkUsernameExists(accountDto.getUsername());
		// if email change, check if email already exist
		if (account.getEmail()!= null && !account.getEmail().equals(accountDto.getEmail()))
			this.checkEmailExists(accountDto.getEmail());
		
		Account updateAccount = accountDto.toAccount();
		updateAccount.setLastUpdated(new Date().getTime());

		updateAccount = this.crudService.update(id, updateAccount, this.getEditableAndSearchableFields());
		logger.info("Updated account {}", updateAccount);
		return this.castAccount(updateAccount);
	}

	public AccountDto changePassword(long id, String newPassword) throws UserNotFoundException {
		Account account = crudService.get(id);

		if (account == null)
			throw new UserNotFoundException();
//		if (!this.passwordEncoder.matches(oldPassword, account.getPassword()))
//			throw new InvalidPasswordException();

		account.setPassword(passwordEncoder.encode(newPassword));
		account.setLastUpdated(new Date().getTime());
		account = this.repository.save(account);
		logger.info("Changed password for account {}", account.getUsername());
		return this.castAccount(account);
	}

	public void deleteAccount(long id) {
		this.crudService.delete(id);
		logger.info("Deleted user for id {}", id);
	}

	public List<AccountDto> search(String query) {
		List<Account> list = this.pageService.search(query);
		return list.stream().map(this::castAccount).collect(Collectors.toList());
	}

	/**
	 * Returns a Page object of this entity. Can be filtered by a static filter or
	 * dynamic search query.
	 * 
	 * @param page      Page index
	 * @param size      Size of elements per page
	 * @param sortBy    Page sorted by
	 * @param ascending Sorted ascending (true) or descending (false)
	 * @param search    Search query
	 * @param filter    RSQL query filter
	 * @return Page
	 */
	public Page<AccountDto> getPage(int page, int size, String sortBy, Boolean ascending, String search,
			String filter) {
		Page<Account> pageObject = this.pageService.getPage(page, size, sortBy, ascending, search, filter,
				this.getEditableAndSearchableFields());
		Page<AccountDto> pageDto = pageObject.map(this::castAccount);
		return pageDto;
	}

	public void addTestAccounts() {
		try {
			if (!this.repository.existsByUsername("adam")) {
				this.createAccount(new NewAccountDto("adam@email.com", "adam", "Adam", "Smith", "password"));
				logger.info("Test Accounts Adam are created");
			}
			if (!this.repository.existsByUsername("frank")) {
				this.createAccount(new NewAccountDto("frank@email.com", "frank", "Frank", "Grüger", "password"));
				logger.info("Test Accounts Frank are created");
			}
			if (!this.repository.existsByUsername("max")) {
				this.createAccount(new NewAccountDto("max@email.com", "max", "Max", "Mustermann", "password"));
				logger.info("Test Accounts Max are created");
			}
			if (!this.repository.existsByUsername("eva")) {
				this.createAccount(new NewAccountDto("eva@email.com", "eva", "Eva", "Maurer", "password"));
				logger.info("Test Accounts Eva are created");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Set<Field> getEditableAndSearchableFields() {
		return new AnnotationService(Account.class)
				.getFieldsWithName(new HashSet<>(Account.ediableAndSearchableFields));
	}

	private AccountDto castAccount(Account account) {
		Account user = this.userService.getAuthenticatedUser();
		if (user != null && user.hasRole(ROLE.ROLE_ADMIN.toString()))
			return new DetailedAccountDto(account);
		else
			return new AccountDto(account);
	}

	private void checkEmailExists(@NonNull String email) {
		if (email.isBlank() || this.repository.existsByEmail(email))
			throw new EmailAlreadyExistException();
	}
	
	private void checkUsernameExists(@NonNull String username) {
		if (username.isBlank() || this.repository.existsByUsername(username))
			throw new UsernameAlreadyExistException();
	}
}
