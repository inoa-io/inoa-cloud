package io.inoa.fleet.registry.domain;

import java.util.List;
import java.util.Set;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.repository.GenericRepository;

/**
 * Repository for {@link GatewayGroup}.
 *
 * @author Stephan Schnabel
 */
@JdbcRepository
public interface GatewayGroupRepository extends GenericRepository<GatewayGroup, Void> {

	List<Group> findGroupByGateway(Gateway gateway);

	void saveAll(Set<GatewayGroup> group);

	void deleteByGatewayAndGroup(Gateway gateway, Group group);
}
