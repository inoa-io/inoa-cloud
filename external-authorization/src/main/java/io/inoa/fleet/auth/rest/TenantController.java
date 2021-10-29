package io.inoa.fleet.auth.rest;

import java.util.List;
import java.util.UUID;

import io.inoa.fleet.auth.domain.TenantRepository;
import io.inoa.fleet.auth.mapper.TenantMapper;
import io.inoa.fleet.auth.rest.management.TenantCreateVO;
import io.inoa.fleet.auth.rest.management.TenantUpdateVO;
import io.inoa.fleet.auth.rest.management.TenantVO;
import io.inoa.fleet.auth.rest.management.TenantsApi;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class TenantController implements TenantsApi {

	private final TenantMapper mapper;
	private final TenantRepository tenantRepository;

	@Override
	public HttpResponse<TenantVO> createTenant(TenantCreateVO tenantCreateVO) {
		return null;
	}

	@Override
	public HttpResponse<Object> deleteTenant(UUID tenantId) {
		return null;
	}

	@Override
	public HttpResponse<TenantVO> findTenant(UUID tenantId) {
		return null;
	}

	@Override
	public HttpResponse<List<TenantVO>> findTenants() {
		return HttpResponse.ok(mapper.toTenants(tenantRepository.findAllOrderByName()));
	}

	@Override
	public HttpResponse<TenantVO> updateTenant(UUID tenantId, TenantUpdateVO tenantUpdateVO) {
		return null;
	}

}
