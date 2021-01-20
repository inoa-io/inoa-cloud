package io.kokuwa.fleet.registry.domain;

import java.util.UUID;

import io.kokuwa.fleet.registry.domain.GatewayGroup.GatewayGroupPK;
import io.micronaut.context.annotation.Requires;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.reactive.RxJavaCrudRepository;
import io.reactivex.Flowable;

/**
 * Repository for {@link GatewayGroup}.
 *
 * @author Stephan Schnabel
 */
public interface GatewayGroupRepository extends RxJavaCrudRepository<GatewayGroup, GatewayGroupPK> {

	Flowable<UUID> findGroupIdByGatewayIdOrderByGatewayId(UUID gatewayId);
}

@Requires(property = "datasources.default.dialect", value = "H2")
@JdbcRepository(dialect = Dialect.H2)
interface GatewayGroupRepositoryH2 extends GatewayGroupRepository {}

@Requires(property = "datasources.default.dialect", value = "POSTGRES")
@JdbcRepository(dialect = Dialect.POSTGRES)
interface GatewayGroupRepositoryPostgres extends GatewayGroupRepository {}

@Requires(property = "datasources.default.dialect", value = "MYSQL")
@JdbcRepository(dialect = Dialect.MYSQL)
interface GatewayGroupRepositoryMysql extends GatewayGroupRepository {}
