package io.kokuwa.fleet.registry.domain;

import java.util.UUID;

import io.micronaut.context.annotation.Requires;
import io.micronaut.data.annotation.Join;
import io.micronaut.data.annotation.Join.Type;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.reactive.RxJavaCrudRepository;
import io.reactivex.Maybe;

/**
 * Repository for {@link Gateway}.
 *
 * @author Stephan Schnabel
 */
public interface GatewayRepository extends RxJavaCrudRepository<Gateway, Long> {

	@Join(value = "tenant", type = Type.FETCH)
	@Join(value = "properties", type = Type.LEFT_FETCH)
	Maybe<Gateway> findByUuid(UUID uuid);
}

@Requires(property = "datasources.default.dialect", value = "H2")
@JdbcRepository(dialect = Dialect.H2)
interface GatewayRepositoryH2 extends GatewayRepository {}
