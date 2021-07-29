package io.kokuwa.fleet.registry.domain;

import java.util.List;
import java.util.Optional;

import io.micronaut.data.annotation.Join;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.repository.GenericRepository;

/**
 * Repository for {@link GatewayConfiguration}.
 *
 * @author Stephan Schnabel
 */
@JdbcRepository
@Join("definition")
public interface GatewayConfigurationRepository extends GenericRepository<GatewayConfiguration, Void> {

	List<GatewayConfiguration> findByGateway(Gateway gateway);

	Optional<GatewayConfiguration> findByGatewayAndDefinition(Gateway gateway, ConfigurationDefinition definition);

	GatewayConfiguration save(GatewayConfiguration gatewayConfiguration);

	@Query("UPDATE gateway_configuration"
			+ " SET value = :value"
			+ " WHERE gateway_id = :gatewayId"
			+ " AND definition_id = :definitionId")
	void updateByGatewayAndDefinition(Long gatewayId, Long definitionId, String value);

	void deleteByGatewayAndDefinition(Gateway gateway, ConfigurationDefinition definition);
}
