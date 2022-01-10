package io.inoa.fleet.thing.domain;

import java.util.List;

import io.micronaut.context.annotation.Requires;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

public interface ThingChannelRepository extends CrudRepository<ThingChannel, Long> {

	List<ThingChannel> findByThing(Thing thing);
}

@Requires(property = "datasources.default.dialect", value = "H2")
@JdbcRepository(dialect = Dialect.H2)
interface ThingChannelRepositoryH2 extends ThingChannelRepository {

}

@Requires(property = "datasources.default.dialect", value = "POSTGRES")
@JdbcRepository(dialect = Dialect.POSTGRES)
interface ThingChannelRepositoryPostgres extends ThingChannelRepository {

}
