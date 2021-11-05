package io.inoa.fleet.auth.domain;

import java.util.List;
import java.util.Optional;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.repository.CrudRepository;

@JdbcRepository
public interface UserRepository extends CrudRepository<User, Long> {

	List<User> findAllOrderByEmail();

	Optional<User> findByEmail(String email);
}
