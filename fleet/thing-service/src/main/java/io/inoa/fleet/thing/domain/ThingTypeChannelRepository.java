package io.inoa.fleet.thing.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import io.micronaut.context.annotation.Requires;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

public interface ThingTypeChannelRepository extends CrudRepository<ThingTypeChannel, Long> {
	List<ThingTypeChannel> findByThingType(ThingType thingType);

	Optional<ThingTypeChannel> findByThingTypeChannelId(UUID thingTypeChannelId);
}

@Requires(property = "datasources.default.dialect", value = "H2")
@JdbcRepository(dialect = Dialect.H2)
interface ThingTypeChannelRepositoryH2 extends ThingTypeChannelRepository {
}

@Requires(property = "datasources.default.dialect", value = "POSTGRES")
@JdbcRepository(dialect = Dialect.POSTGRES)
interface ThingTypeChannelRepositoryPostgres extends ThingTypeChannelRepository {
}
