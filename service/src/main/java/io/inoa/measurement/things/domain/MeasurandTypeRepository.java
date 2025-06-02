package io.inoa.measurement.things.domain;

import java.util.Optional;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface MeasurandTypeRepository extends CrudRepository<MeasurandType, Long> {
	Optional<MeasurandType> findByObisId(String obisId);

	MeasurandType save(String obisId, String name, String description);
}
