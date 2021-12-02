package io.inoa.cnpm.tenant.management;

import org.mapstruct.Mapper;

import io.inoa.cnpm.tenant.domain.Issuer;
import io.inoa.cnpm.tenant.domain.Tenant;
import io.inoa.cnpm.tenant.domain.User;
import io.micronaut.data.model.Page;

/**
 * Mapper for REST level.
 *
 * @author Stephan Schnabel
 */
@Mapper
public interface ManagementMapper {

	UserVO toUser(User user);

	UserPageVO toUsers(Page<User> page);

	TenantVO toTenant(Tenant tenant);

	IssuerVO toIssuer(Issuer issuer);
}
