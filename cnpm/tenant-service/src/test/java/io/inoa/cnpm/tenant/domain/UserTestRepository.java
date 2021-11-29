package io.inoa.cnpm.tenant.domain;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.repository.CrudRepository;

/**
 * Test repository for {@link User}.
 *
 * @author Stephan Schnabel
 */
@JdbcRepository
public interface UserTestRepository extends CrudRepository<User, Long> {}
