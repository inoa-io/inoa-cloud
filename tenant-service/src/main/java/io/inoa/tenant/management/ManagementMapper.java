package io.inoa.tenant.management;

import org.mapstruct.Mapper;

import io.inoa.tenant.domain.Tenant;
import io.inoa.tenant.domain.User;
import io.micronaut.data.model.Page;

/**
 * Mapper for REST level.
 *
 * @author Stephan Schnabel
 */
@Mapper
public interface ManagementMapper {

	UserVO toUser(User user);

	UserPageVO toUserPage(Page<User> page);

	TenantVO toTenant(Tenant tenant);
}
