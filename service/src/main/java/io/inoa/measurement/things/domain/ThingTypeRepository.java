package io.inoa.measurement.things.domain;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import java.util.Optional;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface ThingTypeRepository extends CrudRepository<ThingType, Long> {

  Optional<ThingType> findByThingTypeId(String thingTypeId);

  Page<ThingType> findByNameIlike(String filter, Pageable pageable);

  Page<ThingType> findAll(Pageable pageable);
}
