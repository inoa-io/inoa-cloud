package io.kokuwa.fleet.registry.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import io.micronaut.context.annotation.Requires;
import io.micronaut.data.annotation.Join;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

/**
 * Repository for {@link Group}.
 *
 * @author Stephan Schnabel
 */
public interface GroupRepository extends CrudRepository<Group, Long> {

	List<Group> findByTenantOrderByName(Tenant tenant);

	@Join("tenant")
	Optional<Group> findByTenantAndGroupId(Tenant tenant, UUID groupId);

	Optional<Group> findByTenantTenantIdAndGroupId(UUID tenantId, UUID groupId);

	Boolean existsByTenantAndName(Tenant tenant, String name);

	Boolean existsByTenantAndGroupId(Tenant tenant, UUID groupId);
}

@Requires(property = "datasources.default.dialect", value = "H2")
@JdbcRepository(dialect = Dialect.H2)
interface GroupRepositoryH2 extends GroupRepository {
}

@Requires(property = "datasources.default.dialect", value = "POSTGRES")
@JdbcRepository(dialect = Dialect.POSTGRES)
interface GroupRepositoryPostgres extends GroupRepository {
}

@Requires(property = "datasources.default.dialect", value = "MYSQL")
@JdbcRepository(dialect = Dialect.MYSQL)
interface GroupRepositoryMysql extends GroupRepository {
}
