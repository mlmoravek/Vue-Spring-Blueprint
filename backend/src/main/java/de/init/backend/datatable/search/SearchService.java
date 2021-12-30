package de.init.backend.datatable.search;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.google.common.base.Joiner;

import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.RSQLParserException;
import cz.jirutka.rsql.parser.ast.Node;
import de.init.backend.datatable.search.jpa.CriteriaParser;
import de.init.backend.datatable.search.jpa.SearchOperation;
import de.init.backend.datatable.search.jpa.SearchSpecification;
import de.init.backend.datatable.search.jpa.SearchSpecificationBuilder;
import de.init.backend.datatable.search.rsql.CustomRsqlVisitor;

/**
 * Provide functions to create Specifications from a string. Can used as search api. Throws
 * InvalidDataAccessApiUsageException if attribute name is not found on object
 *
 * @author mmoravek
 */
public class SearchService {

	private static final Logger LOG = LoggerFactory.getLogger(SearchService.class);

	/**
	 * Using simple search expressions. Like: "firstName:john,'lastName:do*" Different operations can be found in
	 * {@link SearchOperation}.
	 * 
	 * @param <T> type of applicable data class
	 * @param query simple search expression
	 * @param searchQuery
	 * @return Specification can be used to perform a JPA query with {@link JpaSpecificationExecutor}.
	 */
	public static <T> Specification<T> createSpecification(String query) {
		try {
			SearchSpecificationBuilder<T> builder = new SearchSpecificationBuilder<>();
			String operationSetExper = Joiner.on("|").join(SearchOperation.SIMPLE_OPERATION_SET);
			Pattern pattern = Pattern.compile(
					"(\\p{Punct}?)(\\w+?\\.?\\w+?)(" + operationSetExper + ")(\\p{Punct}?)(\\w+?)(\\p{Punct}?),");

			Matcher matcher = pattern.matcher(query + ",");
			while (matcher.find()) {
				builder.with(matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(5), matcher.group(4),
						matcher.group(6));
			}

			return builder.build();
		} catch (RuntimeException ex) {
			LOG.error("Error creating Specification width query: " + query, ex);
			throw new SearchQueryParseException();
		}
	}

	/**
	 * Using valid infix expression. Like: "( firstName:john OR firstName:tom ) AND age>22" Individual criteria,
	 * operators & grouping parenthesis are separated with a space. Different operations can be found in
	 * {@link SearchOperation}.
	 *
	 * @param <T> type of applicable data class
	 * @param query infix expression
	 * @param query
	 * @return Specification can be used to perform a JPA query with {@link JpaSpecificationExecutor}.
	 */
	public static <T> Specification<T> createSpecificationFromInfixExpr(String query) {
		try {
			CriteriaParser parser = new CriteriaParser();
			SearchSpecificationBuilder<T> builder = new SearchSpecificationBuilder<>();
			return builder.build(parser.parse(query), SearchSpecification::new);
		} catch (RuntimeException ex) {
			LOG.error("Error creating Specification width query: " + query, ex);
			throw new SearchQueryParseException();
		}
	}

	/**
	 * Using RSQL expressions and the library <a href="https://github.com/jirutka/rsql-parser">rsql-parser</a>. This can
	 * handle nested Objects. Examples: name=="Kill Bill" and year>2003 genres=in=(sci-fi,action) and
	 * (director=='Christopher Nolan' or actor==*Bale) and year>=2000 director.lastName==Nolan;year=ge=2000;year=lt=2010
	 *
	 * @param <T> type of applicable data class
	 * @param query RSQL expression
	 * @return Specification can be used to perform a JPA query with {@link JpaSpecificationExecutor}.
	 */
	public static <T> Specification<T> createSpecificationWithRSQL(String query) {
		try {
			Node rootNode = new RSQLParser().parse(query);
			return rootNode.accept(new CustomRsqlVisitor<>());
		} catch (RSQLParserException ex) {
			LOG.error("SearchService Error: creating Specification width query: " + query, ex);
			throw new SearchQueryParseException();
		}
	}

}
