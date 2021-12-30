package de.init.backend.model.entity;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import de.init.backend.authentication.model.User;
import de.init.backend.datatable.annotation.Editable;
import de.init.backend.datatable.annotation.Searchable;

@Entity
@Table(name = "accounts")
public class Account extends User implements Serializable {

	private static final long serialVersionUID = 1L;

	// need to add @Editiable @Searchable for CrudService at fields, but fields are
	// on superclass so we have define them on another way
	public static final List<String> ediableAndSearchableFields = Arrays.asList("username", "email", "firstName", "lastName",
			"lastUpdated");

	@Editable
	@Searchable
	@Column(name = "email", unique = true)
	protected String email;

	@Editable
	@Searchable
	protected String firstName;

	@Editable
	@Searchable
	protected String lastName;

	public Account() {
		super();
	}

	public Account(User user) {
		this.id = user.getId();
		this.created = user.getCreated();
		this.enabled = user.isEnabled();
		this.lastLogin = user.getLastLogin();
		this.lastUpdated = user.getLastUpdated();
		this.password = user.getPassword();
		this.username = user.getUsername();
		this.roles = user.getRoles();
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@Override
	public String toString() {
		return "Account [id=" + id + ", username=" + username + ", email=" + email + ", firstname=" + firstName
				+ ", lastname=" + lastName + "]";
	}
}
