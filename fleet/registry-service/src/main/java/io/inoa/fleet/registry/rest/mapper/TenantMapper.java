package io.inoa.fleet.registry.rest.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import io.inoa.fleet.registry.domain.Tenant;
import io.inoa.fleet.registry.rest.management.TenantVO;

/**
 * Mapper for {@link Tenant}.
 *
 * @author Stephan Schnabel
 */
@Mapper
public interface TenantMapper {

	TenantVO toTenant(Tenant tenant);

	List<TenantVO> toTenants(List<Tenant> tenants);
}
