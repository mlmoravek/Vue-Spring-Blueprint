package de.init.backend.authentication.model;

import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

/**
 * Represents a low-level, granular privilege/authority in the system. See:
 * https://www.baeldung.com/role-and-privilege-for-spring-security-registration
 * 
 * @author mmoravek
 */

@Entity
@Table(name = "auth_permission")
public class Permission  {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String name;

	@ManyToMany(mappedBy = "permissions", fetch = FetchType.LAZY)
	private Collection<Role> roles;

	public Permission() {
	}

	public Permission(String name) {
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Collection<Role> getRoles() {
		return roles;
	}

	public void setRoles(Collection<Role> roles) {
		this.roles = roles;
	}

	@Override
	public String toString() {
		return "Privilege [name=" + name + "]";
	}

}