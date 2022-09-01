package io.inoa.fleet.registry.domain;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import io.micronaut.data.annotation.Join;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.repository.CrudRepository;

/**
 * Repository for {@link Gateway}.
 *
 * @author Stephan Schnabel
 */
public interface GatewayRepository extends CrudRepository<Gateway, Long> {

	Page<Gateway> findByTenant(Tenant tenant, Optional<String> filter, Pageable pageable);

	@Join("tenant")
	Optional<Gateway> findByGatewayId(UUID gatewayId);

	Optional<Gateway> findByTenantAndGatewayId(Tenant tenant, UUID gatewayId);

	Boolean existsByTenant(Tenant tenant);

	Boolean existsByTenantAndName(Tenant tenant, String name);

	@Query("UPDATE gateway SET mqtt_timestamp=:timestmap,mqtt_connected=:connected WHERE gateway_id=:gatewayId ")
	void updateStatusMqtt(UUID gatewayId, Instant timestmap, boolean connected);
}
