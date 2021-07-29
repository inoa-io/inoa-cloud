package io.kokuwa.fleet.registry.domain;

import java.util.List;
import java.util.Optional;

import io.micronaut.data.annotation.Join;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.repository.GenericRepository;

/**
 * Repository for {@link TenantConfiguration}.
 *
 * @author Stephan Schnabel
 */
@JdbcRepository
@Join("definition")
public interface TenantConfigurationRepository extends GenericRepository<TenantConfiguration, Void> {

	List<TenantConfiguration> findByDefinitionTenant(Tenant tenant);

	Optional<TenantConfiguration> findByDefinition(ConfigurationDefinition definition);

	TenantConfiguration save(TenantConfiguration tenantConfiguration);

	@Query("UPDATE tenant_configuration SET value = :value WHERE definition_id = :definitionId")
	void updateByDefinition(Long definitionId, String value);

	void deleteByDefinition(ConfigurationDefinition definition);
}
