package io.inoa.fleet.registry.rest.mapper;

import java.util.List;
import java.util.UUID;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;

import io.inoa.fleet.model.GroupVO;
import io.inoa.fleet.registry.domain.Group;

/**
 * Mapper for {@link Group}.
 *
 * @author Stephan Schnabel
 */
@Mapper(componentModel = ComponentModel.JAKARTA)
public interface GroupMapper {

	GroupVO toGroup(Group group);

	List<GroupVO> toGroups(List<Group> groups);

	default UUID toGroupId(Group group) {
		return group.getGroupId();
	}
}
