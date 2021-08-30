package io.inoa.fleet.registry.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import io.micronaut.data.annotation.Join;
import io.micronaut.data.annotation.Join.Type;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.repository.CrudRepository;

/**
 * Repository for {@link Credential}.
 *
 * @author Stephan Schnabel
 */
@JdbcRepository
@Join(value = "secrets", type = Type.LEFT_FETCH)
public interface CredentialRepository extends CrudRepository<Credential, Long> {

	List<Credential> findByGateway(Gateway gateway);

	Optional<Credential> findByGatewayAndCredentialId(Gateway gateway, UUID credentialId);

	Optional<Credential> findByGatewayAndAuthId(Gateway gateway, String authId);

	boolean existsByGatewayAndAuthId(Gateway gateway, String authId);
}
