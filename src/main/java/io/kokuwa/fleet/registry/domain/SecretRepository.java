package io.kokuwa.fleet.registry.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import io.micronaut.context.annotation.Requires;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

/**
 * Repository for {@link Secret}.
 *
 * @author Stephan Schnabel
 */
public interface SecretRepository extends CrudRepository<Secret, Long> {

	List<Secret> findByGatewayOrderByName(Gateway gateway);

	Optional<Secret> findByGatewayAndSecretId(Gateway gateway, UUID externalId);

	Boolean existsByGatewayAndName(Gateway gateway, String name);
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
