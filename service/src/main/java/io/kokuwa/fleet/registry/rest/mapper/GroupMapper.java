package io.kokuwa.fleet.registry.rest.mapper;

import java.util.List;
import java.util.UUID;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;

import io.kokuwa.fleet.registry.domain.Group;
import io.kokuwa.fleet.registry.rest.management.GroupVO;

/**
 * Mapper for {@link Group}.
 *
 * @author Stephan Schnabel
 */
@Mapper(componentModel = ComponentModel.JSR330)
public interface GroupMapper {

	GroupVO toGroup(Group group);

	List<GroupVO> toGroups(List<Group> groups);

	default UUID toGroupId(Group group) {
		return group.getGroupId();
	}
}
