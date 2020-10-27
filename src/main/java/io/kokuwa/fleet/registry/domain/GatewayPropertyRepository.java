package io.kokuwa.fleet.registry.domain;

import io.micronaut.context.annotation.Requires;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.reactive.RxJavaCrudRepository;
import io.reactivex.Completable;

/**
 * Repository for {@link GatewayProperty}.
 *
 * @author Stephan Schnabel
 */
public interface GatewayPropertyRepository extends RxJavaCrudRepository<GatewayProperty, Long> {

	default Completable store(GatewayProperty property) {
		return property.getId() == null
				? save(property).ignoreElement()
				: update(property.getId(), property.getValue());
	}

	Completable update(@Id Long id, String value);

	Completable deleteByGateway(Gateway gateway);
}

@Requires(property = "datasources.default.dialect", value = "H2")
@JdbcRepository(dialect = Dialect.H2)
interface GatewayPropertyRepositoryH2 extends GatewayPropertyRepository {}
