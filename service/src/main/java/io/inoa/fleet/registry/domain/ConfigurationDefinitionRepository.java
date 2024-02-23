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
public interface ConfigurationDefinitionRepository extends CrudRepository<ConfigurationDefinition, Long> {

	List<ConfigurationDefinition> findByTenantOrderByKey(Tenant tenant);

	List<ConfigurationDefinition> findByTenantInListOrderByKey(List<Tenant> tenant);

	Optional<ConfigurationDefinition> findByTenantAndKey(Tenant tenant, String key);

	boolean existsByTenantAndKey(Tenant tenant, String key);

	boolean existsByTenantInListAndKey(List<Tenant> tenant, String key);
}
