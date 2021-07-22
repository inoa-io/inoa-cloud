package io.kokuwa.fleet.registry.domain;

import java.util.List;
import java.util.Optional;

import io.kokuwa.fleet.registry.domain.GatewayProperty.GatewayPropertyPK;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.repository.CrudRepository;

/**
 * Repository for {@link GatewayProperty}.
 *
 * @author Stephan Schnabel
 */
@JdbcRepository
public interface GatewayPropertyRepository extends CrudRepository<GatewayProperty, GatewayPropertyPK> {

	List<GatewayProperty> findByGatewayId(Long gatewayId);

	Optional<GatewayProperty> findByGatewayIdAndKey(Long gatewayId, String key);
}
