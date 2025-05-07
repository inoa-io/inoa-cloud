package io.inoa.fleet.registry.rest.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;

import io.inoa.fleet.registry.domain.Tenant;
import io.inoa.rest.TenantVO;

/**
 * Mapper for {@link Tenant}.
 *
 * @author Fabian Schlegel
 */
@Mapper(componentModel = ComponentModel.JAKARTA)
public interface TenantMapper {

	TenantVO toTenant(Tenant tenant);

	List<TenantVO> toTenants(List<Tenant> tenants);
}
