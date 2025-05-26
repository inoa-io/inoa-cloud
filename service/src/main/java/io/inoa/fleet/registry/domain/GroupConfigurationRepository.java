package io.inoa.fleet.registry.domain;

import java.util.List;
import java.util.Optional;

import io.micronaut.data.annotation.Join;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.GenericRepository;

/**
 * Repository for {@link GroupConfiguration}.
 *
 * @author Stephan Schnabel
 */
@JdbcRepository(dialect = Dialect.POSTGRES)
@Join("definition")
public interface GroupConfigurationRepository extends GenericRepository<GroupConfiguration, Long> {

	List<GroupConfiguration> findByGroup(Group group);

	List<GroupConfiguration> findByGroupIn(List<Group> groups);

	Optional<GroupConfiguration> findByGroupAndDefinition(
			Group group, ConfigurationDefinition definition);

	GroupConfiguration save(GroupConfiguration groupConfiguration);

	@Query("UPDATE group_configuration SET value = :value WHERE group_id = :groupId AND definition_id ="
			+ " :definitionId")
	void update(Long groupId, Long definitionId, String value);

	@Query("DELETE FROM group_configuration WHERE group_id = :groupId AND definition_id = :definitionId")
	void delete(Long groupId, Long definitionId);
}
