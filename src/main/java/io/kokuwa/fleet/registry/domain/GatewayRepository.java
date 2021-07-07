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
 * Repository for {@link Gateway}.
 *
 * @author Stephan Schnabel
 */
public interface GatewayRepository extends CrudRepository<Gateway, Long> {

	List<Gateway> findByTenantOrderByName(Tenant tenant);

	@Join("tenant")
	Optional<Gateway> findByGatewayId(UUID gatewayId);

	@Join("tenant")
	Optional<Gateway> findByTenantTenantIdAndGatewayId(UUID tenantId, UUID gatewayId);

	Boolean existsByTenant(Tenant tenant);

	@Join("tenant")
	Boolean existsByTenantAndName(Tenant tenant, String name);
}

@Requires(property = "datasources.default.dialect", value = "H2")
@JdbcRepository(dialect = Dialect.H2)
interface GatewayRepositoryH2 extends GatewayRepository {

}

@Requires(property = "datasources.default.dialect", value = "POSTGRES")
@JdbcRepository(dialect = Dialect.POSTGRES)
interface GatewayRepositoryPostgres extends GatewayRepository {

}

@Requires(property = "datasources.default.dialect", value = "MYSQL")
@JdbcRepository(dialect = Dialect.MYSQL)
interface GatewayRepositoryMysql extends GatewayRepository {

}
