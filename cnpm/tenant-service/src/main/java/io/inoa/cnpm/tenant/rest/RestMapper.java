package io.inoa.cnpm.tenant.rest;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import io.inoa.cnpm.tenant.domain.Assignment;
import io.inoa.cnpm.tenant.domain.Issuer;
import io.inoa.cnpm.tenant.domain.Tenant;
import io.micronaut.data.model.Page;

/**
 * Mapper for REST level.
 */
@Mapper
public interface RestMapper {

	@Mapping(target = "userId", source = "user.userId")
	@Mapping(target = "email", source = "user.email")
	UserVO toUser(Assignment assignment);

	UserPageVO toUsers(Page<Assignment> page);

	TenantVO toTenant(Tenant tenant);

	List<TenantPageEntryVO> toTenants(List<Tenant> tenants);

	IssuerVO toIssuer(Issuer issuer);

	List<IssuerVO> toIssuers(List<Issuer> issuers);
}
