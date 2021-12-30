package de.init.backend.authentication.config;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * @source https://www.baeldung.com/spring-security-create-new-custom-security-expression
 * @source https://dev.to/ashishrameshan/custom-role-based-permission-authorization-in-spring-boot-m7f
 */
@Component
class CustomMethodSecurityExpressionHandler extends DefaultMethodSecurityExpressionHandler {

	@Override
	protected MethodSecurityExpressionOperations createSecurityExpressionRoot(Authentication authentication,
			MethodInvocation invocation) {

		CustomSecurityExpressionRoot root = new CustomSecurityExpressionRoot(authentication);
		root.setPermissionEvaluator(getPermissionEvaluator());
		root.setTrustResolver(getTrustResolver());
		root.setRoleHierarchy(getRoleHierarchy());
		root.setDefaultRolePrefix(getDefaultRolePrefix());
		root.setThis(invocation.getThis());
		return root;
	}
}