package io.inoa.fleet.registry.domain;

import java.util.List;
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

	List<Tenant> findAllOrderByName();

	Optional<Tenant> findByTenantId(String tenantId);

	Optional<String> findTenantIdByName(String name);

	Boolean existsByNameOrTenantId(String name, String tenantId);

	Boolean existsByName(String name);
}
