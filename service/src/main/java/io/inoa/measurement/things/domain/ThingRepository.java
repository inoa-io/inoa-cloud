package io.inoa.measurement.things.domain;

import io.inoa.fleet.registry.domain.Gateway;
import io.micronaut.data.annotation.Join;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import java.util.Optional;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface ThingRepository extends CrudRepository<Thing, Long> {

  @Join("gateway.tenant")
  Optional<Thing> findByNameAndGateway(String name, Gateway tenant);

  @Join("gateway.tenant")
  @Join("thingType")
  @Join(value = "measurands.measurandType", type = Join.Type.OUTER_FETCH)
  @Join(value = "thingConfigurationValues", type = Join.Type.OUTER_FETCH)
  @Join(value = "thingConfigurationValues.thingConfiguration")
  Optional<Thing> findByThingId(UUID thingId);
}
