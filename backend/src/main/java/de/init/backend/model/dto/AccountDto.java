package de.init.backend.model.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import de.init.backend.authentication.model.ResponseDto;
import de.init.backend.authentication.model.Role;
import de.init.backend.model.entity.Account;
import de.init.backend.validation.ValidEmail;

public class AccountDto extends ResponseDto implements Serializable{

	private static final long serialVersionUID = -890845460559173034L;

	// id can be null at new accounts
	protected long id;

	@NotNull
	@NotEmpty
	protected String firstName;

	@NotNull
	@NotEmpty
	protected String lastName;

	@ValidEmail
	@NotNull
	@NotEmpty
	protected String email;

	@NotNull
	@NotEmpty
	protected String username;

	protected ArrayList<String> roles;

	public AccountDto() {
		super(200);
	}

	public AccountDto(@NotNull @NotEmpty Account account) {
		super(200);
		this.id = account.getId();
		this.firstName = account.getFirstName();
		this.lastName = account.getLastName();
		this.email = account.getEmail();
		this.username = account.getUsername();
		this.roles = new ArrayList<>(account.getRoles().stream().map(Role::getName).collect(Collectors.toList()));
	}

	public long getId() {
		return id;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getEmail() {
		return email;
	}

	public String getUsername() {
		return username;
	}

	public List<String> getRoles() {
		return roles;
	}

	public Account toAccount() {
		Account a = new Account();
		a.setEmail(this.email);
		a.setFirstName(this.firstName);
		a.setLastName(this.lastName);
		a.setUsername(this.username);
		return a;
	}

}