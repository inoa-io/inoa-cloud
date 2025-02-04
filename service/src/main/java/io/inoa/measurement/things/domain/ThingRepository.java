package io.inoa.measurement.things.domain;

import io.micronaut.data.annotation.Join;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import java.util.List;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface ThingRepository extends CrudRepository<Thing, Long> {

  @Join(value = "measurands", type = Join.Type.FETCH)
  @Join(value = "thingConfigurationValues", type = Join.Type.FETCH)
  List<Thing> findByThingType(ThingType thingType);

  @Join(value = "measurands", type = Join.Type.FETCH)
  @Join(value = "thingConfigurationValues", type = Join.Type.FETCH)
  Thing findByThingId(UUID thingId);
}
