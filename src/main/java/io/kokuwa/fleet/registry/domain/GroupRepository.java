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
 * Repository for {@link Group}.
 *
 * @author Stephan Schnabel
 */
public interface GroupRepository extends RxJavaCrudRepository<Group, Long> {

	@Join("tenant")
	Flowable<Group> findAllOrderByName();

	@Join("tenant")
	Flowable<Group> findByTenantExternalIdOrderByName(UUID tenantExternalId);

	@Join("tenant")
	Maybe<Group> findByExternalId(UUID externalId);

	Single<Boolean> existsByTenantAndName(Tenant tenant, String name);
}

@Requires(property = "datasources.default.dialect", value = "H2")
@JdbcRepository(dialect = Dialect.H2)
interface GroupRepositoryH2 extends GroupRepository {}

@Requires(property = "datasources.default.dialect", value = "POSTGRES")
@JdbcRepository(dialect = Dialect.POSTGRES)
interface GroupRepositoryPostgres extends GroupRepository {}

@Requires(property = "datasources.default.dialect", value = "MYSQL")
@JdbcRepository(dialect = Dialect.MYSQL)
interface GroupRepositoryMysql extends GroupRepository {}
