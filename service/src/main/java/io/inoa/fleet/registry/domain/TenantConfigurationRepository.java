package io.inoa.fleet.registry.domain;

import java.util.List;
import java.util.Optional;

import io.micronaut.data.annotation.Join;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.GenericRepository;

/**
 * Repository for {@link TenantConfiguration}.
 *
 * @author Stephan Schnabel
 */
@JdbcRepository(dialect = Dialect.POSTGRES)
@Join("definition")
public interface TenantConfigurationRepository
		extends GenericRepository<TenantConfiguration, Long> {

	List<TenantConfiguration> findByTenant(Tenant tenant);

	List<TenantConfiguration> findByTenantInList(List<Tenant> tenant);

	Optional<TenantConfiguration> findByDefinition(ConfigurationDefinition definition);

	TenantConfiguration save(TenantConfiguration tenantConfiguration);

	@Query("UPDATE tenant_configuration SET value = :value WHERE definition_id = :definitionId")
	void update(Long definitionId, String value);

	@Query("DELETE FROM tenant_configuration WHERE definition_id = :definitionId")
	void delete(Long definitionId);
}
