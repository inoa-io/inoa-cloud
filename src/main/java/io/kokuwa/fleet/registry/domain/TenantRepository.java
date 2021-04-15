package io.kokuwa.fleet.registry.domain;

import java.util.UUID;

import io.micronaut.context.annotation.Requires;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.reactive.RxJavaCrudRepository;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;

/**
 * Repository for {@link Tenant}.
 *
 * @author Stephan Schnabel
 */
public interface TenantRepository extends RxJavaCrudRepository<Tenant, Long> {

	Flowable<Tenant> findAllOrderByName();

	Maybe<Tenant> findByExternalId(UUID externalId);

	Single<Boolean> existsByExternalId(UUID externalId);

	Single<Boolean> existsByName(String name);
}

@Requires(property = "datasources.default.dialect", value = "H2")
@JdbcRepository(dialect = Dialect.H2)
interface TenantRepositoryH2 extends TenantRepository {}

@Requires(property = "datasources.default.dialect", value = "POSTGRES")
@JdbcRepository(dialect = Dialect.POSTGRES)
interface TenantRepositoryPostgres extends TenantRepository {}

@Requires(property = "datasources.default.dialect", value = "MYSQL")
@JdbcRepository(dialect = Dialect.MYSQL)
interface TenantRepositoryMysql extends TenantRepository {}
