package io.inoa.fleet.auth.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;

import io.inoa.fleet.auth.domain.User;
import io.inoa.fleet.auth.rest.management.UserVO;

/**
 * Mapper for {@link User}.
 *
 */
@Mapper(componentModel = ComponentModel.JSR330)
public interface UserMapper {

	@Mapping(target = "id", source = "userId")
	UserVO toUser(User user);

	List<UserVO> toUsers(List<User> users);
}
