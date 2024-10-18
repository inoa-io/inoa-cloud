package io.inoa.fleet.registry.domain;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for {@link Credential}.
 *
 * @author Stephan Schnabel
 */
@JdbcRepository(dialect = Dialect.POSTGRES)
public interface CredentialRepository extends CrudRepository<Credential, Long> {

  List<Credential> findByGateway(Gateway gateway);

  Optional<Credential> findByGatewayAndCredentialId(Gateway gateway, UUID credentialId);

  Optional<Credential> findByGatewayAndName(Gateway gateway, String name);

  boolean existsByGatewayAndName(Gateway gateway, String name);
}
