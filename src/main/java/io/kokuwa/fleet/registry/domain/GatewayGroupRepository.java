package io.kokuwa.fleet.registry.domain;

import java.util.List;

import io.kokuwa.fleet.registry.domain.GatewayGroup.GatewayGroupPK;
import io.micronaut.context.annotation.Requires;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

/**
 * Repository for {@link GatewayGroup}.
 *
 * @author Stephan Schnabel
 */
public interface GatewayGroupRepository extends CrudRepository<GatewayGroup, GatewayGroupPK> {

	@Query("SELECT g.* FROM \"group\" g, gateway_group gg WHERE gg.group_id=g.id AND gg.gateway_id=:gatewayId")
	List<Group> findGroupsByGatewayId(Long gatewayId);
}

@Requires(property = "datasources.default.dialect", value = "H2")
@JdbcRepository(dialect = Dialect.H2)
interface GatewayGroupRepositoryH2 extends GatewayGroupRepository {}

@Requires(property = "datasources.default.dialect", value = "POSTGRES")
@JdbcRepository(dialect = Dialect.POSTGRES)
interface GatewayGroupRepositoryPostgres extends GatewayGroupRepository {}

@Requires(property = "datasources.default.dialect", value = "MYSQL")
@JdbcRepository(dialect = Dialect.MYSQL)
interface GatewayGroupRepositoryMysql extends GatewayGroupRepository {}
