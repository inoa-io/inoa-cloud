package io.inoa.measurement.things.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import io.micronaut.data.annotation.Join;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface ThingRepository extends CrudRepository<Thing, Long> {

	Optional<Thing> findByThingId(UUID thingId);

	@Join(value = "thingType", type = Join.Type.FETCH)
	Optional<Thing> findByThingIdAndTenantId(UUID thingId, String tenantId);

	@Join(value = "thingType", type = Join.Type.FETCH)
	Page<Thing> findByTenantId(String tenantId, Pageable pageable);

	@Join(value = "thingType", type = Join.Type.FETCH)
	Page<Thing> findByTenantIdAndGatewayId(String tenantId, String gatewayId, Pageable pageable);

	@Join(value = "thingType", type = Join.Type.FETCH)
	List<Thing> findAllByTenantIdAndGatewayId(String tenantId, String gatewayId);

	Page<Thing> findAll(Pageable pageable);
}
