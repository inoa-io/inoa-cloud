package io.inoa.fleet.thing.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import io.micronaut.context.annotation.Requires;
import io.micronaut.data.annotation.Join;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

public interface ThingRepository extends CrudRepository<Thing, Long> {

	Optional<Thing> findByThingId(UUID thingId);

	@Join(value = "thingType", type = Join.Type.FETCH)
	Optional<Thing> findByThingIdAndTenantId(UUID thingId, String tenantId);

	Page<Thing> findByNameIlike(String filter, Pageable pageable);

	Page<Thing> findByTenantId(String tenantId, Pageable pageable);

	Page<Thing> findByTenantIdAndGatewayId(String tenantId, UUID gatewayId, Pageable pageable);

	@Join(value = "thingType", type = Join.Type.FETCH)
	List<Thing> findAllByTenantIdAndGatewayId(String tenantId, UUID gatewayId);

	Page<Thing> findAll(Pageable pageable);
}

@Requires(property = "datasources.default.dialect", value = "H2")
@JdbcRepository(dialect = Dialect.H2)
interface ThingRepositoryH2 extends ThingRepository {

}

@Requires(property = "datasources.default.dialect", value = "POSTGRES")
@JdbcRepository(dialect = Dialect.POSTGRES)
interface ThingRepositoryPostgres extends ThingRepository {

}
