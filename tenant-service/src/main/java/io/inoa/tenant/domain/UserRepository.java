package io.inoa.tenant.domain;

import java.util.Optional;
import java.util.UUID;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.repository.GenericRepository;

/**
 * Repository for {@link User}.
 *
 * @author Stephan Schnabel
 */
@JdbcRepository
public interface UserRepository extends GenericRepository<User, Long> {

	Optional<User> findByEmail(String email);

	User save(User user);

	void deleteByUserId(UUID userId);

}
