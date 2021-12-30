package de.init.backend.authentication.config;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.CollectionUtils;

import de.init.backend.authentication.UserPrinciple;

/**
 * Note – We're using the Permission – Role terms here, but in Spring, these are
 * slightly different. In Spring, our Permission is referred to as Role, and
 * also as a (granted) authority – which is slightly confusing. Not a problem
 * for the implementation of course, but definitely worth noting.
 * 
 * Also – these Spring Roles (our Permissions) need a prefix; by default, that
 * prefix is “ROLE”, but it can be changed. We're not using that prefix here,
 * just to keep things simple, but keep in mind that if you're not explicitly
 * changing it, it's going to be required.
 * 
 * We now have two new security expression available and ready to be used:
 * hasPermission & hasAnyPermission. Similarly how Spring Security has inbuilt
 * expressions like hasRole & hasAnyRole , we can check permissions as well.
 * 
 * hasPermission - @param- String[] , checks if current user has ALL
 * permissions.
 * 
 * hasAnyPermission - @param- String[], checks if Current User has ANY
 * permission.
 * 
 * @source https://www.baeldung.com/spring-security-create-new-custom-security-expression
 * @source https://dev.to/ashishrameshan/custom-role-based-permission-authorization-in-spring-boot-m7f
 */
class CustomSecurityExpressionRoot extends SecurityExpressionRoot implements MethodSecurityExpressionOperations {

	private Object filterObject;
	private Object returnObject;
	private Object target;

	/**
	 * Creates a new instance
	 *
	 * @param authentication the {@link Authentication} to use. Cannot be null.
	 */
	public CustomSecurityExpressionRoot(Authentication authentication) {
		super(authentication);
	}

	/**
	 *  This creates the custom annotation rule hasAnyPermission
	 */
	public boolean hasAnyPermission(String... permissions) {
		UserPrinciple authentication = (UserPrinciple) getPrincipal();
		for (String permission : permissions) {
			if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(permission))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * This creates the custom annotation rule hasPermission
	 * Validates if Current User is authorized for ALL given permissions
	 *
	 * @param permissions cannot be empty
	 */
	public boolean hasPermission(String... permissions) {
		UserPrinciple authentication = (UserPrinciple) getPrincipal();
		if (!CollectionUtils.isEmpty(authentication.getAuthorities())) {
			List<String> authenticationPermissions = authentication.getAuthorities().stream().filter(Objects::nonNull)
					.map(GrantedAuthority::getAuthority).collect(Collectors.toList());

			return Arrays.stream(permissions).filter(permission -> !permission.isBlank())
					.allMatch(authenticationPermissions::contains);
		}
		return false;
	}

	@Override
	public void setFilterObject(Object filterObject) {
		this.filterObject = filterObject;
	}

	@Override
	public Object getFilterObject() {
		return filterObject;
	}

	@Override
	public void setReturnObject(Object returnObject) {
		this.returnObject = returnObject;
	}

	@Override
	public Object getReturnObject() {
		return returnObject;
	}

	@Override
	public Object getThis() {
		return target;
	}

	public void setThis(Object target) {
		this.target = target;
	}
}