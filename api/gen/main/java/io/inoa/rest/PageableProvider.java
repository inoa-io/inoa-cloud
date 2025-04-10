package io.inoa.rest;

import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.micronaut.context.annotation.Requires;
import io.micronaut.core.naming.NameUtils;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.Sort;
import io.micronaut.data.model.Sort.Order;
import io.micronaut.data.model.Sort.Order.Direction;
import io.micronaut.data.runtime.config.DataConfiguration.PageableConfiguration;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.context.ServerRequestContext;
import io.micronaut.http.exceptions.HttpStatusException;

/**
 * Inspired by micronauts PageableRequestArgumentBinder.
 * Added functionality to validate sort strings and define default sort.
 *
 * @author Stephan Schnabel
 * @see io.micronaut.data.runtime.http.PageableRequestArgumentBinder
 */
@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")

@jakarta.inject.Singleton
@Requires(beans = PageableConfiguration.class)
public class PageableProvider {

	private final PageableConfiguration configuration;

	public PageableProvider(PageableConfiguration configuration) {
		this.configuration = configuration;
	}

	/**
	 * Get pageable from request with pagination.
	 *
	 * @return {@link Pageable}.
	 */
	public Pageable getPageable() {
		return getPageable(Map.of());
	}

	/**
	 * Get pageable from request with pagination and sorting.
	 *
	 * @param sortOrderProperties possible sort orders
	 * @return {@link Pageable}.
	 */
	public Pageable getPageable(Set<String> sortOrderProperties) {
		return getPageable(sortOrderProperties, null);
	}

	/**
	 * Get pageable from request with pagination and sorting using default sort.
	 *
	 * @param sortOrderProperties      possible sort orders
	 * @param defaultSortOrderProperty default sort order to use
	 * @return {@link Pageable}.
	 */
	public Pageable getPageable(Set<String> sortOrderProperties, String defaultSortOrderProperty) {
		return getPageable(
				sortOrderProperties.stream().collect(Collectors.toMap(Function.identity(), NameUtils::camelCase)),
				defaultSortOrderProperty);
	}

	/**
	 * Get pageable from request with pagination and sorting.
	 *
	 * @param sortOrderProperties possible sort orders with external to internal mapping
	 * @return {@link Pageable}.
	 */
	public Pageable getPageable(Map<String, String> sortOrderProperties) {
		return getPageable(sortOrderProperties, null);
	}

	/**
	 * Get pageable from request with pagination and sorting using default sort.
	 *
	 * @param sortOrderProperties      possible sort orders with external to internal mapping
	 * @param defaultSortOrderProperty default sort order to use
	 * @return {@link Pageable}.
	 */
	public Pageable getPageable(Map<String, String> sortOrderProperties, String defaultSortOrderProperty) {
		var parameters = ServerRequestContext.currentRequest().get().getParameters();

		// get pagination values

		var pageNumber = parameters.get(configuration.getPageParameterName(), Integer.class)
				.map(page -> Math.max(0, page))
				.orElse(0);
		var pageSize = parameters.get(configuration.getSizeParameterName(), Integer.class)
				.map(size -> Math.min(size, configuration.getMaxPageSize()))
				.orElse(configuration.getDefaultPageSize());
		if (sortOrderProperties.isEmpty()) {
			return Pageable.from(pageNumber, pageSize);
		}

		// map order properties

		var sortIgnoreCase = configuration.isSortIgnoreCase();
		var sortOrders = parameters.getAll(configuration.getSortParameterName()).stream()
				.map(configuration.getSortDelimiterPattern()::split)
				.map(tokens -> {

					var property = sortOrderProperties.get(tokens[0]);
					var direction = Direction.ASC;

					if (property == null) {
						throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Sort order property " + tokens[0]
								+ " invalid. Valid options: " + sortOrderProperties.keySet());
					}

					if (tokens.length > 1) {
						try {
							direction = Direction.valueOf(tokens[1].toUpperCase(Locale.ENGLISH));
						} catch (IllegalArgumentException e) {
							// sort asc if parsing fails
						}
					}

					return new Order(property, direction, sortIgnoreCase);
				})
				.collect(Collectors.toList());

		// add default order at end if not present

		if (defaultSortOrderProperty != null
				&& sortOrders.stream().map(Order::getProperty).noneMatch(defaultSortOrderProperty::equals)) {
			sortOrders.add(new Order(defaultSortOrderProperty));
		}

		return Pageable.from(pageNumber, pageSize, Sort.of(sortOrders));
	}
}
