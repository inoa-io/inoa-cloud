package io.inoa.fleet.registry.domain;

import io.micronaut.data.annotation.Join;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.GenericRepository;
import java.util.List;
import java.util.Optional;

/**
 * Repository for {@link GatewayConfiguration}.
 *
 * @author Stephan Schnabel
 */
@JdbcRepository(dialect = Dialect.POSTGRES)
@Join("definition")
public interface GatewayConfigurationRepository
    extends GenericRepository<GatewayConfiguration, Void> {

  List<GatewayConfiguration> findByGateway(Gateway gateway);

  Optional<GatewayConfiguration> findByGatewayAndDefinition(
      Gateway gateway, ConfigurationDefinition definition);

  GatewayConfiguration save(GatewayConfiguration gatewayConfiguration);

  @Query(
      "UPDATE gateway_configuration"
          + " SET value = :value"
          + " WHERE gateway_id = :gatewayId"
          + " AND definition_id = :definitionId")
  void update(Long gatewayId, Long definitionId, String value);

  @Query(
      "DELETE FROM  gateway_configuration WHERE gateway_id = :gatewayId AND definition_id ="
          + " :definitionId")
  void delete(Long gatewayId, Long definitionId);
}
