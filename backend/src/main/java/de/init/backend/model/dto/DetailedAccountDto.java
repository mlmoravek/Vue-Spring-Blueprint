package de.init.backend.model.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import de.init.backend.model.entity.Account;

public class DetailedAccountDto extends AccountDto {

	private static final long serialVersionUID = -2684579651302995027L;

	@NotNull
	@NotEmpty
	protected Long lastLogin;

	@NotNull
	@NotEmpty
	protected Long created;

	@NotNull
	@NotEmpty
	protected Long lastUpdated;

	@NotNull
	protected boolean enabled;

	public DetailedAccountDto() {
		super();
	}

	public DetailedAccountDto(@NotNull @NotEmpty Account account) {
		super(account);
		this.lastLogin = account.getLastLogin();
		this.created = account.getCreated();
		this.lastUpdated = account.getLastUpdated();
		this.enabled = account.isEnabled();
	}

	public Account toAccount() {
		Account a = super.toAccount();
		a.setCreated(this.created);
		a.setEnabled(this.enabled);
		a.setLastLogin(this.lastLogin);
		a.setLastUpdated(this.lastUpdated);
		return a;
	}

}