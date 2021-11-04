package io.inoa.fleet.auth.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.repository.CrudRepository;

/**
 * Repository for {@link Tenant}.
 *
 */
@JdbcRepository
public interface TenantRepository extends CrudRepository<Tenant, Long> {

	List<Tenant> findAllOrderByTenantId();

	Optional<Tenant> findByTenantId(String tenantId);

	Boolean existsByTenantId(String tenantId);
}
