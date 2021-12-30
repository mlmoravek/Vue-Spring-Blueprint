package de.init.backend.datatable.search;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Which queries are possible could be read here:
 * https://github.com/jirutka/rsql-parser
 */
public class SearchParser {

	private final Pattern numberPattern = Pattern.compile("-?\\d+(\\.\\d+)?");
	private final List<Field> fields ;
	
	/**
	 * @param fields fields to search on
	 */
	public SearchParser(List<Field> fields) {
		this.fields = fields;
	}
	
	/**
	 * Parse a search term for the given fields
	 * 
	 * @param term search term
	 * @return search query
	 */
	public String parse(String term) {
		if (term.isBlank() || term.equals("undefined")) {
			return "";
		}
	
		boolean isNumber = this.isNumeric(term);
		List<Field> parseFields = new ArrayList<>(this.fields);
		
		if (!isNumber) {
			// filter only string fields
			parseFields = parseFields.stream().filter(field -> field.getType().equals(String.class)).collect(Collectors.toList());
		}
		
		String query = parseFields
				.stream()
				.map(field -> createQuery(field.getName(), field.getType() == String.class ? parseTextTerm(term) : term))
				.collect(Collectors.joining(","));

		return query;
	}

	private String createQuery(String key, String term) {
		return key + "==" + term;
	}

	private String parseTextTerm(String term) {
		// if contains "
		if (term.contains("\"")) {
			// replace " with \"
			term = term.replace("\"", "\\" + "\"");
		}
		// make like term
		term = "*" + term + "*";
		// exact term
		if (term.contains(" ") || term.contains("'")) {
			// set "term"
			term = '"' + term + '"';
		}
		return term;
	}

	/**
	 * this method could be changed to handle other input
	 * https://www.baeldung.com/java-check-string-number
	 * 
	 * @param s String
	 * @return is number (boolean)
	 */
	public boolean isNumeric(String s) {
		if (s == null) {
			return false;
		}
		return numberPattern.matcher(s).matches();
	}

}
