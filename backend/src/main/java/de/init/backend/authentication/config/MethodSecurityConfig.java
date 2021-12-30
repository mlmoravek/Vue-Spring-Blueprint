package de.init.backend.authentication.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

/**
 * GlobalMethodSecurityConfiguration is base Configuration for enabling global
 * method security. Classes may extend this class to customize the defaults, but
 * must be sure to specify the EnableGlobalMethodSecurity annotation on the
 * subclass.
 * 
 * @source https://www.baeldung.com/spring-security-create-new-custom-security-expression
 * @source https://dev.to/ashishrameshan/custom-role-based-permission-authorization-in-spring-boot-m7f
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {

	@Override
	protected MethodSecurityExpressionHandler createExpressionHandler() {
		return new CustomMethodSecurityExpressionHandler();
	}
}