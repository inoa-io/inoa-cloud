package io.inoa.cnpm.tenant.domain;

import java.util.Optional;
import java.util.UUID;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.repository.GenericRepository;

/**
 * Repository for {@link User}.
 */
@JdbcRepository
public interface UserRepository extends GenericRepository<User, Long> {

	Optional<User> findByEmail(String email);

	User save(String email);

	void deleteByUserId(UUID userId);
}
