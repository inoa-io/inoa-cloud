package io.inoa.cloud.backup.domain;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.repository.CrudRepository;

/**
 * Repository for {@link Message}.
 *
 * @author Stephan Schnabel
 */
@JdbcRepository
public interface MessageRepository extends CrudRepository<Message, Long> {}
