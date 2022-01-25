package io.inoa.cnpm.tenant.domain;

import io.micronaut.context.annotation.Secondary;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.repository.CrudRepository;

/**
 * Test repository for {@link User}.
 */
@Secondary
@JdbcRepository
public interface UserTestRepository extends UserRepository, CrudRepository<User, Long> {}
