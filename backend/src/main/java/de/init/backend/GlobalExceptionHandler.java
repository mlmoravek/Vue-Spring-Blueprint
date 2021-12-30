package de.init.backend;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import de.init.backend.authentication.error.InvalidPasswordException;
import de.init.backend.authentication.error.UserNotEnabledException;
import de.init.backend.authentication.error.UserNotFoundException;
import de.init.backend.authentication.error.UsernameAlreadyExistException;
import de.init.backend.authentication.model.ErrorResponseDto;
import de.init.backend.datatable.search.SearchQueryParseException;
import de.init.backend.error.EmailAlreadyExistException;

/**
 * @source https://mkyong.com/spring-boot/spring-rest-validation-example/
 */
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
	private final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	/**
	 * error handler for @Valid
	 */
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {

		ErrorResponseDto errorResponse = new ErrorResponseDto(status.value(), "Validation Failure",
				request.getDescription(false), request.getContextPath());
		ex.getAllErrors().forEach(x -> errorResponse.addError(x.getDefaultMessage()));
		ex.getBindingResult().getFieldErrors().forEach(x -> errorResponse.addError(x.getDefaultMessage()));

		log.info("Validation Error: {}", request.getParameterMap());
		return new ResponseEntity<>(errorResponse, headers, status);
	}

	/**
	 * Error handler for {@link AccessDeniedException}
	 */
	@ExceptionHandler(AccessDeniedException.class)
	public final ResponseEntity<ErrorResponseDto> handleAccessDeniedException(RuntimeException ex, WebRequest request) {
		HttpStatus status = HttpStatus.FORBIDDEN;
		ErrorResponseDto errorResponse = new ErrorResponseDto(status.value(), "Zugriff verweigert!",
				request.getDescription(false), ((ServletWebRequest) request).getRequest().getRequestURI());
		errorResponse.addError(ex.getMessage());
		return new ResponseEntity<>(errorResponse, status);
	}

	/**
	 * Error handler for {@link DataIntegrityViolationException}
	 */
	@ExceptionHandler({ DataIntegrityViolationException.class })
	public final ResponseEntity<ErrorResponseDto> handleConstraintException(Throwable ex, WebRequest request) {
		while (ex.getCause() != null) {
			ex = ex.getCause();
		}
		return this.createResponse(ex, request, HttpStatus.BAD_REQUEST);
	}

	/**
	 * Error handler for authentication exceptions {@link InvalidPasswordException},
	 * {@link UsernameAlreadyExistException}, {@link UserNotEnabledException},
	 * {@link UserNotFoundException}, {@link EmailAlreadyExistException}
	 */
	@ExceptionHandler({ InvalidPasswordException.class, UsernameAlreadyExistException.class,
			UserNotEnabledException.class, UserNotFoundException.class , EmailAlreadyExistException.class})
	public final ResponseEntity<ErrorResponseDto> handleAuthenticationException(RuntimeException ex,
			WebRequest request) {
		return this.createResponse(ex, request, HttpStatus.BAD_REQUEST);
	}

	/**
	 * Error handler for search exceptions {@link SearchQueryParseException},
	 * {@link IllegalArgumentException}
	 */
	@ExceptionHandler({ SearchQueryParseException.class, IllegalArgumentException.class })
	public final ResponseEntity<ErrorResponseDto> handleSearchQueryParseException(RuntimeException ex,
			WebRequest request) {
		return this.createResponse(ex, request, HttpStatus.BAD_REQUEST);
	}

	/**
	 * error handler for path variables validation for @Validated
	 */
	@ExceptionHandler(ConstraintViolationException.class)
	public void constraintViolationException(HttpServletResponse response) throws IOException {
		response.sendError(HttpStatus.BAD_REQUEST.value(), "Validierungsfehler");
	}

	private ResponseEntity<ErrorResponseDto> createResponse(Throwable ex, WebRequest request, HttpStatus status) {
		ErrorResponseDto errorResponse = new ErrorResponseDto(status.value(), ex.getMessage(),
				request.getParameterMap().toString(), ((ServletWebRequest) request).getRequest().getRequestURI());
		return new ResponseEntity<>(errorResponse, status);
	}

}