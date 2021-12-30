package de.init.backend.model.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import de.init.backend.model.entity.Account;
import de.init.backend.validation.PasswordMatches;
import de.init.backend.validation.ValidPassword;

@PasswordMatches
public class NewAccountDto extends AccountDto {

	private static final long serialVersionUID = 8443268798483661353L;

	@ValidPassword
	@NotNull
	@NotEmpty
	private String password;
	private String confirmPassword;

	public NewAccountDto() {}
	
	public NewAccountDto(@NotNull @NotEmpty String email, @NotNull @NotEmpty String username,
			@NotNull @NotEmpty String firstName, @NotNull @NotEmpty String lastName,
			@NotNull @NotEmpty String password) {
		this.email = email;
		this.firstName =firstName;
		this.lastName= lastName;
		this.username = username;
		this.password = password;
	}

	public String getPassword() {
		return password;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	@Override
	public Account toAccount() {
		Account a = super.toAccount();
		a.setPassword(this.getPassword());
		return a;
	}
}