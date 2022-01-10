package io.inoa.fleet.registry.domain;

import java.util.List;
import java.util.Optional;

import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.repository.GenericRepository;

/**
 * Repository for {@link GatewayProperty}.
 *
 * @author Stephan Schnabel
 */
@JdbcRepository
public interface GatewayPropertyRepository extends GenericRepository<GatewayProperty, Void> {

	List<GatewayProperty> findByGateway(Gateway gateway);

	Optional<GatewayProperty> findByGatewayAndKey(Gateway gateway, String key);

	GatewayProperty save(GatewayProperty gatewayProperty);

	@Query("UPDATE gateway_property SET value = :value WHERE gateway_id = :gatewayId AND key = :key")
	void update(Long gatewayId, String key, String value);

	@Query("DELETE FROM gateway_property WHERE gateway_id = :gatewayId AND key = :key")
	void delete(Long gatewayId, String key);
}
