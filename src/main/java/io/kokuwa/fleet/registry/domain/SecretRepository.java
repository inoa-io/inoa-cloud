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
 * Repository for {@link Secret}.
 *
 * @author Stephan Schnabel
 */
public interface SecretRepository extends RxJavaCrudRepository<Secret, UUID> {

	Flowable<Secret> findByGatewayOrderByName(Gateway gateway);

	Maybe<Secret> findByGatewayAndExternalId(Gateway gateway, UUID externalId);

	Single<Boolean> existsByGatewayAndName(Gateway gateway, String name);
}

@Requires(property = "datasources.default.dialect", value = "H2")
@JdbcRepository(dialect = Dialect.H2)
interface SecretRepositoryH2 extends SecretRepository {}

@Requires(property = "datasources.default.dialect", value = "POSTGRES")
@JdbcRepository(dialect = Dialect.POSTGRES)
interface SecretRepositoryPostgres extends SecretRepository {}

@Requires(property = "datasources.default.dialect", value = "MYSQL")
@JdbcRepository(dialect = Dialect.MYSQL)
interface SecretRepositoryMysql extends SecretRepository {}
