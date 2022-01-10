package io.inoa.fleet.registry.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.repository.CrudRepository;

/**
 * Repository for {@link Group}.
 *
 * @author Stephan Schnabel
 */
@JdbcRepository
public interface GroupRepository extends CrudRepository<Group, Long> {

	List<Group> findByTenantOrderByName(Tenant tenant);

	Optional<Group> findByTenantAndGroupId(Tenant tenant, UUID groupId);

	Boolean existsByTenantAndName(Tenant tenant, String name);

	Boolean existsByTenantAndGroupId(Tenant tenant, UUID groupId);
}
