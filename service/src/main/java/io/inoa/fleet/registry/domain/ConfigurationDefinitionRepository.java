package io.inoa.fleet.registry.domain;

import java.util.List;
import java.util.Optional;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.repository.CrudRepository;

/**
 * Repository for {@link ConfigurationDefinition}.
 *
 * @author Stephan Schnabel
 */
@JdbcRepository
public interface ConfigurationDefinitionRepository extends CrudRepository<ConfigurationDefinition, Long> {

	List<ConfigurationDefinition> findByTenantOrderByKey(Tenant tenant);

	Optional<ConfigurationDefinition> findByTenantAndKey(Tenant tenant, String key);

	boolean existsByTenantAndKey(Tenant tenant, String key);
}
