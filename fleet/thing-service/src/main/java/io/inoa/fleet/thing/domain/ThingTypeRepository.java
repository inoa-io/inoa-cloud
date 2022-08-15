package io.inoa.fleet.thing.domain;

import java.util.Optional;
import java.util.UUID;

import io.micronaut.context.annotation.Requires;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

public interface ThingTypeRepository extends CrudRepository<ThingType, Long> {

	Optional<ThingType> findByThingTypeId(UUID thingId);

	Page<ThingType> findByNameIlike(String filter, Pageable pageable);

	Page<ThingType> findAll(Pageable pageable);
}

@Requires(property = "datasources.default.dialect", value = "H2")
@JdbcRepository(dialect = Dialect.H2)
interface ThingTypeRepositoryH2 extends ThingTypeRepository {

}

@Requires(property = "datasources.default.dialect", value = "POSTGRES")
@JdbcRepository(dialect = Dialect.POSTGRES)
interface ThingTypeRepositoryPostgres extends ThingTypeRepository {

}
