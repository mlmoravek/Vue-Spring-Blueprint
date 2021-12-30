package de.init.backend.datatable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;
import javax.persistence.Id;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;

import de.init.backend.datatable.annotation.AnnotationService;
import de.init.backend.datatable.annotation.Searchable;
import de.init.backend.datatable.search.SearchParser;
import de.init.backend.datatable.search.SearchService;

/**
 * This class could not be annotated created to a bean, because the repository
 * has to be chosen by runtime.
 *
 * @param <T>
 */
public class PageService<T, I> {

	private final DataTableRepository<T, I> repository;

	private final String idField;

	private final AnnotationService annotationService;

	/**
	 * Create PageService instance.
	 *
	 * @param repository The repository to work on
	 */
	public PageService(DataTableRepository<T, I> repository, Class<T> typeClass) {
		this.repository = repository;
		this.annotationService = new AnnotationService(typeClass);

		Set<Field> idFields = annotationService.getFieldsWithAnnotation(Id.class);
		if (idFields.size() != 1) {
			throw new IllegalArgumentException("Entity must have one ID field");
		}
		idField = idFields.iterator().next().getName();
	}

	/**
	 * Creates a new {@link PageRequestBuilder} based on this service.
	 *
	 * @return a new instance of page request builder
	 */
	public PageRequestBuilder<T, I> builder() {
		return new PageRequestBuilder<>(this);
	}

	/**
	 * Return a simple page object on T.
	 *
	 * @param page      Page index
	 * @param size      Page size
	 * @param sortBy    Sort by column
	 * @param ascending ASC - true or DESC - false
	 * @return Page<T>
	 */
	public Page<T> getPage(int page, int size, String sortBy, Boolean ascending) {
		PageRequest pageable = this.buildPageRequest(page, size, sortBy, ascending);
		return this.repository.findAll(pageable);
	}

	/**
	 * Return a page object on T. You can specify a search query on
	 * {@link @Searchable} properties. You can specify a specific filter via RSQL
	 * statements.
	 *
	 * @param page      Page index
	 * @param size      Page size
	 * @param sortBy    Sort by column
	 * @param ascending ASC - true or DESC - false
	 * @param search    Search query string
	 * @param filter    RSQL query string
	 * @return Page<T>
	 */
	public Page<T> getPage(int page, int size, String sortBy, Boolean ascending, String search, String filter) {
		return this.getPage(page, size, sortBy, ascending, search, filter, null);
	}

	Page<T> getPage(Specification<T> specification, Pageable pageable) {
		return this.repository.findAll(specification, pageable);
	}

	/**
	 * Return a page object on T. You can specify a search query on
	 * searchableFields. You can specify a specific filter via RSQL statements.
	 *
	 * @param page             Page index
	 * @param size             Page size
	 * @param sortBy           Sort by column
	 * @param ascending        ASC - true or DESC - false
	 * @param search           Search query string
	 * @param filter           RSQL query string
	 * @param searchableFields Set of Field to search in
	 * @return Page<T>
	 */
	public Page<T> getPage(int page, int size, String sortBy, Boolean ascending, String search, String filter,
			@Nullable Set<Field> searchableFields) {
		// create pageable object by page properties
		PageRequest pageable = this.buildPageRequest(page, size, sortBy, ascending);

		Specification<T> searchSpecification = null;
		// create Specification on serach value
		if (search != null && !search.isBlank()) {
			String searchQuery = this.buildEntitySearchQuery(search, searchableFields);
			searchSpecification = SearchService.createSpecificationWithRSQL(searchQuery);
		}

		Specification<T> filterSpecification = null;
		// create Specification on filter value
		if (filter != null && !filter.isBlank()) {
			filterSpecification = SearchService.createSpecificationWithRSQL(filter);
		}

		Specification<T> spec = Specification.where(searchSpecification).and(filterSpecification);
		return getPage(spec, pageable);
	}

	/**
	 * Return a list of elements which fits to the query.
	 *
	 * @param search Search query string for search service
	 * @return List of T
	 */
	public List<T> search(String search) {
		return this.search(search, null);
	}

	/**
	 * Return a list of elements which fits to the query.
	 *
	 * @param search           Search query string for search service
	 * @param searchableFields Set of Field to search in
	 * @return List of T
	 */
	public List<T> search(String search, @Nullable Set<Field> searchableFields) {
		String searchQuery = this.buildEntitySearchQuery(search, searchableFields);
		Specification<T> spec = SearchService.createSpecificationWithRSQL(searchQuery);
		return this.repository.findAll(spec);
	}

	/**
	 * Build a PageRequest Object with the given parameter
	 *
	 * @param page      Page index
	 * @param size      Page size
	 * @param sortBy    Sorted by
	 * @param ascending Ordering ascending
	 * @return PageRequest
	 */
	PageRequest buildPageRequest(int page, int size, String sortBy, Boolean ascending) {
		Sort sort = null;
		// check if sortby field exists 
		if (!sortBy.isEmpty() && this.annotationService.getFieldWithName(sortBy).isPresent()) {
			// create sort if set
			// NOTE: For OracleDB a unique field needs to be specified when sorting
			// otherwise paging via Spring Pagable does not work in all cases. So we add the
			// id field here:
			sort = Sort.by(sortBy, idField);
			// add ordering
			sort = Boolean.TRUE.equals(ascending) ? sort.ascending() : sort.descending();
		}
		// create pageable request
		return sort != null ? PageRequest.of(page, size, sort) : PageRequest.of(page, size);
	}

	/**
	 * Create a RSQL query term containing a like query for each {@link @Searchable}
	 * property for T.
	 *
	 * @param term Search term
	 * @return RSQL term
	 */
	String buildEntitySearchQuery(@NonNull String term, @Nullable Set<Field> searchableFields) {
		if (term.isEmpty()) {
			return "";
		}

		if (searchableFields == null)
			// get all @Searchable annotated properties
			searchableFields = this.annotationService.getFieldsWithAnnotation(Searchable.class);

		// create equal likes statements for each @Searchable annotated property
		return new SearchParser(new ArrayList<>(searchableFields)).parse(term);
	}

}
