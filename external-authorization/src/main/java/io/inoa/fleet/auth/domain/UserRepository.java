package io.inoa.fleet.auth.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.repository.CrudRepository;

/**
 * Repository for {@link Tenant}.
 *
 * @author Stephan Schnabel
 */
@JdbcRepository
public interface UserRepository extends CrudRepository<User, Long> {

	List<User> findAllOrderByEmail();

	Optional<User> findByUserId(UUID userId);

	Optional<UUID> findUserIdByEmail(String email);

	Boolean existsByEmailOrUserId(String email, UUID userId);

	Boolean existsByEmail(String email);
}
