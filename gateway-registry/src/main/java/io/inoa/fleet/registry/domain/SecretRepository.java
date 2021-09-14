package io.inoa.fleet.registry.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.repository.CrudRepository;

/**
 * Repository for {@link Secret}.
 *
 * @author Stephan Schnabel
 */
@JdbcRepository
public interface SecretRepository extends CrudRepository<Secret, Long> {

	List<Secret> findByCredential(Credential credential);

	Optional<Secret> findByCredentialAndSecretId(Credential credential, UUID secretId);
}
