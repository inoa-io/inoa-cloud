package io.inoa.measurement.things.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import io.inoa.fleet.registry.domain.Gateway;
import io.micronaut.data.annotation.Join;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface ThingRepository extends CrudRepository<Thing, Long> {

	boolean existsByNameAndGateway(String name, Gateway tenant);

	@Join("gateway.tenant")
	@Join("thingType.measurandTypes")
	@Join("thingType.thingConfigurations")
	@Join(value = "measurands.measurandType", type = Join.Type.OUTER_FETCH)
	@Join(value = "thingConfigurationValues", type = Join.Type.OUTER_FETCH)
	@Join(value = "thingConfigurationValues.thingConfiguration")
	Optional<Thing> findByThingId(UUID thingId);

	@Join("gateway.tenant")
	@Join("thingType.measurandTypes")
	@Join("thingType.thingConfigurations")
	@Join(value = "measurands.measurandType", type = Join.Type.OUTER_FETCH)
	@Join(value = "thingConfigurationValues", type = Join.Type.OUTER_FETCH)
	@Join(value = "thingConfigurationValues.thingConfiguration")
	List<Thing> findByGateway(Gateway gateway);
}
