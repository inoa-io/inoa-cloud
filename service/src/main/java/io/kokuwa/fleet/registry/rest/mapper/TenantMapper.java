package io.kokuwa.fleet.registry.rest.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;

import io.kokuwa.fleet.registry.domain.Tenant;
import io.kokuwa.fleet.registry.rest.management.TenantVO;

/**
 * Mapper for {@link Tenant}.
 *
 * @author Stephan Schnabel
 */
@Mapper(componentModel = ComponentModel.JSR330)
public interface TenantMapper {

	TenantVO toTenant(Tenant tenant);

	List<TenantVO> toTenants(List<Tenant> tenants);
}
