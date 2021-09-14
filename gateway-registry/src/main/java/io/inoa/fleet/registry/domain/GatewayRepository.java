package io.inoa.fleet.registry.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import io.micronaut.data.annotation.Join;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.repository.CrudRepository;

/**
 * Repository for {@link Gateway}.
 *
 * @author Stephan Schnabel
 */
@JdbcRepository
public interface GatewayRepository extends CrudRepository<Gateway, Long> {

	List<Gateway> findByTenantOrderByName(Tenant tenant);

	@Join("tenant")
	Optional<Gateway> findByGatewayId(UUID gatewayId);

	Optional<Gateway> findByTenantAndGatewayId(Tenant tenant, UUID gatewayId);

	Boolean existsByTenant(Tenant tenant);

	Boolean existsByTenantAndName(Tenant tenant, String name);
}
