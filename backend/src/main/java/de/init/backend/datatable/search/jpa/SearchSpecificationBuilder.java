package de.init.backend.datatable.search.jpa;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;

/**
 * @source https://github.com/eugenp/tutorials/blob/ef9cf2e238e5227f37b39a9ba2ec01e11041177c/spring-rest-query-language/src/main/java/com/baeldung/persistence/dao/UserSpecificationsBuilder.java
 *
 * @param <T>
 */
public class SearchSpecificationBuilder<T> {

	private final List<SearchCriteria> params;

	public SearchSpecificationBuilder() {
		params = new ArrayList<>();
	}

	public SearchSpecificationBuilder(@NonNull List<SearchCriteria> filters) {
		this.params = filters;
	}

	// API

	public final SearchSpecificationBuilder<T> with(SearchCriteria criteria) {
		params.add(criteria);
		return this;
	}

	public final SearchSpecificationBuilder<T> with(final String key, final String operation, final Object value,
			final String prefix, final String suffix) {
		return with(null, key, operation, value, prefix, suffix);
	}

	public final SearchSpecificationBuilder<T> with(final String orPredicate, final String key, final String operation,
			final Object value, final String prefix, final String suffix) {
		SearchCriteria criteria = new SearchCriteria(orPredicate, key, operation, prefix, value, suffix);
		params.add(criteria);
		return this;
	}

	public Specification<T> build() {
		if (params.size() == 0)
			return null;

		Specification<T> result = new SearchSpecification<>(params.get(0));

		for (int i = 1; i < params.size(); i++) {
			result = params.get(i).isOrPredicate()
					? Specification.where(result).or(new SearchSpecification<>(params.get(i)))
					: Specification.where(result).and(new SearchSpecification<>(params.get(i)));
		}

		return result;
	}

	public Specification<T> build(Function<SearchCriteria, Specification<T>> converter) {
		if (params.size() == 0) {
			return null;
		}

		final List<Specification<T>> specs = params.stream().map(converter)
				.collect(Collectors.toCollection(ArrayList::new));

		Specification<T> result = specs.get(0);

		for (int idx = 1; idx < specs.size(); idx++) {
			result = params.get(idx).isOrPredicate() ? Specification.where(result).or(specs.get(idx))
					: Specification.where(result).and(specs.get(idx));
		}

		return result;
	}

	public Specification<T> build(Deque<?> postFixedExprStack, Function<SearchCriteria, Specification<T>> converter) {
		Deque<Specification<T>> specStack = new LinkedList<>();

		Collections.reverse((List<?>) postFixedExprStack);

		while (!postFixedExprStack.isEmpty()) {
			Object mayBeOperand = postFixedExprStack.pop();

			if (!(mayBeOperand instanceof String)) {
				specStack.push(converter.apply((SearchCriteria) mayBeOperand));
			} else {
				Specification<T> operand1 = specStack.pop();
				Specification<T> operand2 = specStack.pop();
				if (mayBeOperand.equals(SearchOperation.AND_OPERATOR))
					specStack.push(Specification.where(operand1).and(operand2));
				else if (mayBeOperand.equals(SearchOperation.OR_OPERATOR))
					specStack.push(Specification.where(operand1).or(operand2));
			}

		}
		return specStack.pop();

	}
}
