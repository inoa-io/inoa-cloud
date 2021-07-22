package io.kokuwa.fleet.registry.domain;

import java.util.List;

import io.kokuwa.fleet.registry.domain.GatewayGroup.GatewayGroupPK;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.repository.CrudRepository;

/**
 * Repository for {@link GatewayGroup}.
 *
 * @author Stephan Schnabel
 */
@JdbcRepository
public interface GatewayGroupRepository extends CrudRepository<GatewayGroup, GatewayGroupPK> {

	@Query("SELECT g.* FROM \"group\" g, gateway_group gg WHERE gg.group_id=g.id AND gg.gateway_id=:gatewayId")
	List<Group> findGroupsByGatewayId(Long gatewayId);
}
