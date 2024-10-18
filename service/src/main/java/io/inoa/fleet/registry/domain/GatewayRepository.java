package io.inoa.fleet.registry.domain;

import io.micronaut.data.annotation.Join;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.repository.CrudRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Repository for {@link Gateway}.
 *
 * @author Stephan Schnabel
 */
public interface GatewayRepository extends CrudRepository<Gateway, Long> {

  Page<Gateway> findByTenant(Tenant tenant, Optional<String> filter, Pageable pageable);

  Page<Gateway> findByTenantInList(
      List<Tenant> tenants, Optional<String> filter, Pageable pageable);

  @Join("tenant")
  Optional<Gateway> findByGatewayId(String gatewayId);

  Optional<Gateway> findByTenantAndGatewayId(Tenant tenant, String gatewayId);

  Optional<Gateway> findByTenantInListAndGatewayId(List<Tenant> tenant, String gatewayId);

  Boolean existsByTenant(Tenant tenant);

  @Query(
      "UPDATE gateway SET mqtt_timestamp=:timestmap,mqtt_connected=:connected WHERE"
          + " gateway_id=:gatewayId ")
  void updateStatusMqtt(String gatewayId, Instant timestmap, boolean connected);
}
