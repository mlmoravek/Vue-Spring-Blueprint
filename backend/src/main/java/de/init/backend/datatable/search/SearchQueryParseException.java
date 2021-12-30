package de.init.backend.datatable.search;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class SearchQueryParseException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public SearchQueryParseException(String message) {
		super(message);
	}

	public SearchQueryParseException() {
		super("Could not parse search query.");
	}
}
