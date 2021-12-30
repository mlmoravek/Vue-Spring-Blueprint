package de.init.backend.authentication.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

public class ErrorResponseDto extends ResponseDto{

	private static final long serialVersionUID = 1641829168658655962L;
	
	private String error;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private List<String> errors;
	private String message;
	private String path;

	public ErrorResponseDto(int status) {
		super(status);
	}

	public ErrorResponseDto(int status, String error) {
		super(status);
		this.error = error;
	}

	public ErrorResponseDto(int status, String error, String message, String path) {
		super(status);
		this.error = error;
		this.message = message;
		this.path = path;
	}

	public ErrorResponseDto addError(String error) {
		if (this.errors == null)
			this.errors = new ArrayList<>();
		this.errors.add(error);
		return this;
	}

	public List<String> getErrors() {
		return errors;
	}

	public String getError() {
		return error;
	}

	public String getMessage() {
		return message;
	}

	public String getPath() {
		return path;
	}

}
