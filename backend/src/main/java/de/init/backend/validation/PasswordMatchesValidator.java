package de.init.backend.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import de.init.backend.model.dto.NewAccountDto;


/** 
 * @source https://www.baeldung.com/registration-with-spring-mvc-and-spring-security
 */
public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {

	@Override
	public void initialize(final PasswordMatches constraintAnnotation) {
		//
	}

	@Override
	public boolean isValid(final Object obj, final ConstraintValidatorContext context) {
		final NewAccountDto user = (NewAccountDto) obj;
		return user.getPassword().equals(user.getConfirmPassword());
	}

}