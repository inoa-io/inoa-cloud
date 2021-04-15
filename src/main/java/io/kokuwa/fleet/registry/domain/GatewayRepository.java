package io.kokuwa.fleet.registry.domain;

import java.util.UUID;

import io.micronaut.context.annotation.Requires;
import io.micronaut.data.annotation.Join;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.reactive.RxJavaCrudRepository;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;

/**
 * Repository for {@link Gateway}.
 *
 * @author Stephan Schnabel
 */
public interface GatewayRepository extends RxJavaCrudRepository<Gateway, Long> {

	@Join("tenant")
	Flowable<Gateway> findAllOrderByName();

	@Join("tenant")
	Flowable<Gateway> findByTenantExternalIdOrderByName(UUID tenantExternalId);

	@Join("tenant")
	Maybe<Gateway> findByExternalId(UUID externalId);

	Single<Boolean> existsByExternalId(UUID externalId);

	Single<Boolean> existsByTenant(Tenant tenant);

	Single<Boolean> existsByTenantAndName(Tenant tenant, String name);
}

@Requires(property = "datasources.default.dialect", value = "H2")
@JdbcRepository(dialect = Dialect.H2)
interface GatewayRepositoryH2 extends GatewayRepository {}

@Requires(property = "datasources.default.dialect", value = "POSTGRES")
@JdbcRepository(dialect = Dialect.POSTGRES)
interface GatewayRepositoryPostgres extends GatewayRepository {}

@Requires(property = "datasources.default.dialect", value = "MYSQL")
@JdbcRepository(dialect = Dialect.MYSQL)
interface GatewayRepositoryMysql extends GatewayRepository {}
