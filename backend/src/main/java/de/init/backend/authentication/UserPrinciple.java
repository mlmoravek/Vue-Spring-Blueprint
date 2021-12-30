package de.init.backend.authentication;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.init.backend.authentication.model.Permission;
import de.init.backend.authentication.model.Role;
import de.init.backend.authentication.model.User;

/**
 * Note – We're using the Permissions – Role terms here, but in Spring, these
 * are slightly different. In Spring, our Permission is referred to as Role, and
 * also as a (granted) authority – which is slightly confusing. Not a problem
 * for the implementation of course, but definitely worth noting.
 * 
 * Also – these Spring Roles (our Permissions) need a prefix; by default, that
 * prefix is “ROLE_”, but it can be changed.
 * 
 * @source https://dev.to/ashishrameshan/custom-role-based-permission-authorization-in-spring-boot-m7f
 * @source https://www.baeldung.com/role-and-privilege-for-spring-security-registration
 * @source https://ducmanhphan.github.io/2019-02-20-Problem-about-role-name-in-Spring-Security/
 */
public class UserPrinciple implements UserDetails {

	private static final long serialVersionUID = -8032009699723029953L;

	private final Long id;

	private final String username;

	@JsonIgnore
	private final boolean enabled;

	@JsonIgnore
	private final String password;

	private final Collection<? extends GrantedAuthority> authorities;

	private final Collection<Role> roles;

	public UserPrinciple(Long id, String username, String password, boolean enabled, Collection<Role> roles) {
		this.id = id;
		this.username = username;
		this.password = password;
		this.enabled = enabled;
		this.roles = roles;
		this.authorities = this.getAuthorities(roles);
	}

	public UserPrinciple(User user) {
		this.id = user.getId();
		this.username = user.getUsername();
		this.password = user.getPassword();
		this.enabled = user.isEnabled();
		this.roles = user.getRoles();
		this.authorities = this.getAuthorities(user.getRoles());
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	public Long getId() {
		return id;
	}

	public Collection<Role> getRoles() {
		return roles;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return enabled;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		UserPrinciple user = (UserPrinciple) o;
		return Objects.equals(id, user.id);
	}

	private Collection<? extends GrantedAuthority> getAuthorities(Collection<Role> roles) {
		return this.getGrantedAuthorities(getPermissions(roles));
	}

	private List<String> getPermissions(Collection<Role> roles) {
		return roles.stream().map(Role::getPrivileges).flatMap(Collection::stream).map(Permission::getName)
				.collect(Collectors.toList());
	}

	private List<GrantedAuthority> getGrantedAuthorities(List<String> permissions) {
		return permissions.stream().map(permission -> new SimpleGrantedAuthority(permission))
				.collect(Collectors.toList());
	}

}