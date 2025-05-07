package io.inoa.fleet.registry.domain;

import java.util.List;
import java.util.Optional;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

/**
 * Repository for {@link ConfigurationDefinition}.
 *
 * @author Stephan Schnabel
 */
@JdbcRepository(dialect = Dialect.POSTGRES)
public interface ConfigurationDefinitionRepository
		extends CrudRepository<ConfigurationDefinition, Long> {

	List<ConfigurationDefinition> findAllOrderByKey();

	Optional<ConfigurationDefinition> findByKey(String key);

	boolean existsByKey(String key);
}
