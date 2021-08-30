package io.inoa.fleet.registry.domain;

import java.util.List;
import java.util.Optional;

import io.micronaut.data.annotation.Join;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.repository.GenericRepository;

/**
 * Repository for {@link GroupConfiguration}.
 *
 * @author Stephan Schnabel
 */
@JdbcRepository
@Join("definition")
public interface GroupConfigurationRepository extends GenericRepository<GroupConfiguration, Void> {

	List<GroupConfiguration> findByGroup(Group group);

	List<GroupConfiguration> findByGroupIn(List<Group> groups);

	Optional<GroupConfiguration> findByGroupAndDefinition(Group group, ConfigurationDefinition definition);

	GroupConfiguration save(GroupConfiguration groupConfiguration);

	@Query("UPDATE group_configuration SET value = :value WHERE group_id = :groupId AND definition_id = :definitionId")
	void updateByGroupAndDefinition(Long groupId, Long definitionId, String value);

	void deleteByGroupAndDefinition(Group group, ConfigurationDefinition definition);
}
