package de.init.backend.datatable.search.jpa;

/**
 * @link https://github.com/eugenp/tutorials/blob/master/spring-rest-query-language/src/main/java/com/baeldung/web/util/SpecSearchCriteria.java
 */
public class SearchCriteria {

	/** key: the field name – for example, firstName, age, … etc. **/
	private String key;
	/**
	 * operation: the operation – for example, equality, less than, … etc.
	 **/
	private SearchOperation operation;
	/**
	 * value: the field value – for example, john, 25, … etc.
	 **/
	private Object value;

	private boolean orPredicate;
	private boolean isNested;

	public SearchCriteria(final String key, final SearchOperation operation, final Object value) {
		this.key = key;
		this.operation = operation;
		this.value = value;
		this.orPredicate = false;
		this.isNested = (key.contains("."));
	}

	public SearchCriteria(final String orPredicate, final String key, final SearchOperation operation,
			final Object value) {
		this.orPredicate = orPredicate != null && orPredicate.equals(SearchOperation.OR_PREDICATE_FLAG);
		this.isNested = (key.contains("."));
		this.key = key;
		this.operation = operation;
		this.value = value;
	}

	public SearchCriteria(final String orPredicate, String key, String operation, String prefix, Object value,
			String suffix) {
		SearchOperation op = SearchOperation.getSimpleOperation(operation.charAt(0));
		if (op != null && op == SearchOperation.EQUALITY) { // the operation may be complex operation
			final boolean startWithAsterisk = prefix != null && prefix.contains(SearchOperation.ZERO_OR_MORE_REGEX);
			final boolean endWithAsterisk = suffix != null && suffix.contains(SearchOperation.ZERO_OR_MORE_REGEX);

			if (startWithAsterisk && endWithAsterisk) {
				op = SearchOperation.CONTAINS;
			} else if (startWithAsterisk) {
				op = SearchOperation.ENDS_WITH;
			} else if (endWithAsterisk) {
				op = SearchOperation.STARTS_WITH;
			}
		}

		this.isNested = (key.contains("."));
		this.orPredicate = orPredicate != null && orPredicate.equals(SearchOperation.OR_PREDICATE_FLAG);
		this.key = key;
		this.operation = op;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public SearchOperation getOperation() {
		return operation;
	}

	public void setOperation(SearchOperation operation) {
		this.operation = operation;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public boolean isOrPredicate() {
		return orPredicate;
	}

	public void setOrPredicate(boolean orPredicate) {
		this.orPredicate = orPredicate;
	}

	public boolean isNested() {
		return isNested;
	}

	public void setNested(boolean isNested) {
		this.isNested = isNested;
	}

}