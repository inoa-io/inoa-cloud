package io.inoa.measurement.things.domain;

import io.inoa.fleet.registry.domain.Tenant;
import io.micronaut.data.annotation.Join;
import io.micronaut.data.annotation.TenantId;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import java.util.Optional;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface ThingRepository extends CrudRepository<Thing, Long> {

  Page<Thing> findByThingType(ThingType thingType, Pageable pageable);

	@Join("tenant")
	@Join(value = "measurands", type = Join.Type.LEFT_FETCH)
	@Join(value = "thingConfigurationValues", type = Join.Type.LEFT_FETCH)
	Optional<Thing> findByNameAndTenant(String name, Tenant tenant);

	@Join("tenant")
	@Join(value = "measurands", type = Join.Type.LEFT_FETCH)
	@Join(value = "thingConfigurationValues", type = Join.Type.LEFT_FETCH)
	Optional<Thing> findByName(String name);

	@Join("tenant")
	@Join(value = "measurands", type = Join.Type.RIGHT)
  Optional<Thing> findByThingId(UUID thingId);

	@Join("tenant")
	@Join(value = "measurands", type = Join.Type.LEFT_FETCH)
	@Join(value = "thingConfigurationValues", type = Join.Type.LEFT_FETCH)
  Page<Thing> findByNameLike(Optional<String> name, Pageable pageable);
}
