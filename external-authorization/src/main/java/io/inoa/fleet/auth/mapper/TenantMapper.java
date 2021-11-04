package io.inoa.fleet.auth.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;

import io.inoa.fleet.auth.domain.Tenant;
import io.inoa.fleet.auth.rest.management.TenantVO;

/**
 * Mapper for {@link Tenant}.
 *
 */
@Mapper(componentModel = ComponentModel.JSR330)
public interface TenantMapper {

	TenantVO toTenant(Tenant tenant);

	List<TenantVO> toTenants(List<Tenant> tenants);
}
