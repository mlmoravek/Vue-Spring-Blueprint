package de.init.backend.datatable;

import de.init.backend.datatable.search.SearchService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.Set;

/**
 * Builder pattern for {@link PageService} for simplified access.
 *
 * @param <T> Class type of repository
 * @param <I> ID type of class
 */
public class PageRequestBuilder<T, I> {

    private final PageService<T, I> pageService;

    private Pageable pageable = PageRequest.of(0, 10);

    private Specification<T> specification;

    private Combiner combiner = Combiner.AND;

    PageRequestBuilder(PageService<T, I> pageService) {
        this.pageService = pageService;
    }

    /**
     * Creates a {@link Pageable} of given arguments.
     *
     * @param page      page index
     * @param size      page size
     * @param sortBy    sort by column
     * @param ascending ASC - true; DESC - false
     */
    public PageRequestBuilder<T, I> page(int page, int size, String sortBy, Boolean ascending) {
        this.pageable = pageService.buildPageRequest(page, size, sortBy, ascending);
        return this;
    }

    /**
     * Uses given {@link Pageable} for the repository query.
     */
    public PageRequestBuilder<T, I> page(Pageable pageable) {
        this.pageable = pageable;
        return this;
    }

    /**
     * Combines the upcoming {@link Specification}s with logical AND
     */
    public PageRequestBuilder<T, I> and() {
        this.combiner = Combiner.AND;
        return this;
    }

    /**
     * Combines the upcoming {@link Specification}s with logical OR
     */
    public PageRequestBuilder<T, I> or() {
        this.combiner = Combiner.OR;
        return this;
    }

    /**
     * Combines the given {@link Specification} depending on the chosen {@link Combiner}.
     *
     * @param newSpecification the specification to be added
     */
    public PageRequestBuilder<T, I> filter(Specification<T> newSpecification) {

        if (specification == null) {
            specification = newSpecification;
        } else {

            if (combiner == Combiner.AND) {
                specification = specification.and(newSpecification);
            } else {
                specification = specification.or(newSpecification);
            }
        }

        return this;
    }

    /**
     * Creates a {@link Specification} that matches given string <i>search</i> on given fields <i>searchableFields</i>.
     *
     * @param search           Search string
     * @param searchableFields Set of Field to search in
     */
    public PageRequestBuilder<T, I> filter(String search, @Nullable Set<Field> searchableFields) {
        // create Specification on search value
        if (search != null && !search.isBlank()) {
            String searchQuery = pageService.buildEntitySearchQuery(search, searchableFields);
            return filter(SearchService.createSpecificationWithRSQL(searchQuery));

        }
        return this;
    }

    /**
     * Creates a {@link Specification} that matches given <i>rsqlExpression</i>
     *
     * @param rsqlExpression RSQL expression used to create the {@link Specification}
     */
    public PageRequestBuilder<T, I> filter(String rsqlExpression) {
        // create Specification on filter value
        if (rsqlExpression != null && !rsqlExpression.isBlank()) {
            return filter(SearchService.createSpecificationWithRSQL(rsqlExpression));
        }
        return this;
    }

    /**
     * Executes the configured query of page and specification on the repository.
     *
     * @return the selected page
     */
    public Page<T> execute() {
        return pageService.getPage(specification, pageable);
    }

    /**
     * Enumeration to chose in which manner multiple {@link Specification}s are combined to one.
     */
    enum Combiner {
        AND, OR
    }

}
