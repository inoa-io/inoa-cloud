package io.inoa.measurement.things.domain;

import io.inoa.fleet.registry.domain.Gateway;
import io.micronaut.data.annotation.Join;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface ThingRepository extends CrudRepository<Thing, Long> {

  boolean existsByThingId(UUID id);

  boolean existsByNameAndGateway(String name, Gateway tenant);

  @Join("gateway.tenant")
  @Join("thingType")
  @Join(value = "measurands.measurandType", type = Join.Type.OUTER_FETCH)
  @Join(value = "thingConfigurationValues", type = Join.Type.OUTER_FETCH)
  @Join(value = "thingConfigurationValues.thingConfiguration")
  Optional<Thing> findByThingId(UUID thingId);

  @Join("gateway.tenant")
  @Join("thingType")
  @Join(value = "measurands.measurandType", type = Join.Type.OUTER_FETCH)
  @Join(value = "thingConfigurationValues", type = Join.Type.OUTER_FETCH)
  @Join(value = "thingConfigurationValues.thingConfiguration")
  List<Thing> findByGateway(Gateway gateway);
}
