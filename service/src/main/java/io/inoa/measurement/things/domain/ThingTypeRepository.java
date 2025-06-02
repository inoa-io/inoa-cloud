package io.inoa.measurement.things.domain;

import java.util.List;

import io.micronaut.data.annotation.Join;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface ThingTypeRepository extends CrudRepository<ThingType, Long> {

	@Join(value = "measurandTypes", type = Join.Type.FETCH)
	@Join(value = "thingConfigurations", type = Join.Type.FETCH)
	List<ThingType> findByIdentifier(String identifier);

	@Join(value = "measurandTypes", type = Join.Type.FETCH)
	@Join(value = "thingConfigurations", type = Join.Type.FETCH)
	void deleteByIdentifier(String identifier);
}
