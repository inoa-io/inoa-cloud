package io.kokuwa.fleet.registry.domain;

import io.kokuwa.fleet.registry.domain.GatewayProperty.GatewayPropertyPK;
import io.micronaut.context.annotation.Requires;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.reactive.RxJavaCrudRepository;
import io.reactivex.Flowable;
import io.reactivex.Maybe;

/**
 * Repository for {@link GatewayProperty}.
 *
 * @author Stephan Schnabel
 */
public interface GatewayPropertyRepository extends RxJavaCrudRepository<GatewayProperty, GatewayPropertyPK> {

	Flowable<GatewayProperty> findByGatewayId(Long gatewayId);

	Maybe<GatewayProperty> findByGatewayIdAndKey(Long gatewayId, String key);
}

@Requires(property = "datasources.default.dialect", value = "H2")
@JdbcRepository(dialect = Dialect.H2)
interface GatewayPropertyRepositoryH2 extends GatewayPropertyRepository {}

@Requires(property = "datasources.default.dialect", value = "POSTGRES")
@JdbcRepository(dialect = Dialect.POSTGRES)
interface GatewayPropertyRepositoryPostgres extends GatewayPropertyRepository {}

@Requires(property = "datasources.default.dialect", value = "MYSQL")
@JdbcRepository(dialect = Dialect.MYSQL)
interface GatewayPropertyRepositoryMysql extends GatewayPropertyRepository {}
