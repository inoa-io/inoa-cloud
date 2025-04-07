package io.inoa.measurement.things.domain;

import io.micronaut.data.annotation.Join;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import java.util.Optional;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface ThingRepository extends CrudRepository<Thing, Long> {

  @Join(value = "measurands", type = Join.Type.FETCH)
  @Join(value = "thingConfigurationValues", type = Join.Type.FETCH)
  Page<Thing> findByThingType(ThingType thingType, Pageable pageable);

  @Join(value = "measurands", type = Join.Type.FETCH)
  @Join(value = "thingConfigurationValues", type = Join.Type.FETCH)
  Optional<Thing> findByThingId(UUID thingId);

  @Join(value = "measurands", type = Join.Type.FETCH)
  @Join(value = "thingConfigurationValues", type = Join.Type.FETCH)
  Page<Thing> findByNameLike(Optional<String> name, Pageable pageable);
}
