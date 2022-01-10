package io.inoa.fleet.registry.domain;

import java.util.Optional;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.repository.CrudRepository;

/**
 * Repository for {@link Tenant}.
 *
 * @author Stephan Schnabel
 */
@JdbcRepository
public interface TenantRepository extends CrudRepository<Tenant, Long> {

	Optional<Tenant> findByTenantIdAndDeletedIsNull(String tenantId);

	Optional<Tenant> findByTenantId(String tenantId);
}
