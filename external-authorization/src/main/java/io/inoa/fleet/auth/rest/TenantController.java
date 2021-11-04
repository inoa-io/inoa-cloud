package io.inoa.fleet.auth.rest;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import io.inoa.fleet.auth.domain.Tenant;
import io.inoa.fleet.auth.domain.TenantRepository;
import io.inoa.fleet.auth.mapper.TenantMapper;
import io.inoa.fleet.auth.rest.management.TenantCreateVO;
import io.inoa.fleet.auth.rest.management.TenantUpdateVO;
import io.inoa.fleet.auth.rest.management.TenantVO;
import io.inoa.fleet.auth.rest.management.TenantsApi;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.exceptions.HttpStatusException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class TenantController implements TenantsApi {

	private final TenantMapper mapper;
	private final TenantRepository tenantRepository;

	@Override
	public HttpResponse<TenantVO> createTenant(TenantCreateVO vo) {
		// check id/name for uniqueness

		var uniqueSingle = tenantRepository.existsByTenantId(vo.getTenantId());
		if (uniqueSingle) {
			throw new HttpStatusException(HttpStatus.CONFLICT, "Already exists.");
		}

		// create tenant
		var tenant = tenantRepository.save(new Tenant().setName(vo.getName()).setTenantId(vo.getTenantId()));

		// return
		return HttpResponse.created(mapper.toTenant(tenant));
	}

	@Override
	public HttpResponse<Object> deleteTenant(String tenantId) {
		return null;
	}

	@Override
	public HttpResponse<TenantVO> findTenant(String tenantId) {
		var tenant = tenantRepository.findByTenantId(tenantId);
		if (tenant.isEmpty()) {
			throw new HttpStatusException(HttpStatus.NOT_FOUND, "Tenant not found.");
		}
		return HttpResponse.ok(mapper.toTenant(tenant.get()));
	}

	@Override
	public HttpResponse<List<TenantVO>> findTenants() {
		return HttpResponse.ok(mapper.toTenants(tenantRepository.findAllOrderByTenantId()));
	}

	@Override
	public HttpResponse<TenantVO> updateTenant(String tenantId, TenantUpdateVO vo) {
		var optionalTenant = tenantRepository.findByTenantId(tenantId);
		if (optionalTenant.isEmpty()) {
			throw new HttpStatusException(HttpStatus.NOT_FOUND, "Tenant not found.");
		}
		var changed = new AtomicBoolean(false);
		var tenant = optionalTenant.get();
		if (vo.getName() != null) {
			if (tenant.getName().equals(vo.getName())) {
				log.trace("Tenant {}: skip update of name because not changed.", tenant.getName());
			} else {
				log.info("Tenant {}: updated name to {}.", tenant.getName(), vo.getName());
				changed.set(true);
				tenant.setName(vo.getName());
			}
		}
		// return updated

		if (changed.get()) {
			tenant = tenantRepository.update(tenant);
		}
		return HttpResponse.ok(mapper.toTenant(tenant));
	}
}
