package io.inoa.fleet.auth.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;

import io.inoa.fleet.auth.domain.User;
import io.inoa.fleet.auth.rest.management.UserPageVO;
import io.inoa.fleet.auth.rest.management.UserVO;
import io.micronaut.data.model.Page;

/**
 * Mapper for {@link User}.
 *
 */
@Mapper(componentModel = ComponentModel.JSR330)
public interface UserMapper {

	@Mapping(source = "userId", target = "id")
	UserVO toUser(User user);

	UserPageVO toUserPage(Page<User> page);
}
