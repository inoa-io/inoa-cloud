package io.inoa.fleet.registry.rest.management;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import io.inoa.fleet.registry.domain.GatewayRepository;
import io.inoa.fleet.registry.domain.Tenant;
import io.inoa.fleet.registry.domain.TenantRepository;
import io.inoa.fleet.registry.rest.mapper.TenantMapper;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.exceptions.HttpStatusException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of {@link TenantsApi}.
 *
 * @author Stephan Schnabel
 */
@Controller
@Slf4j
@RequiredArgsConstructor
public class TenantsController implements TenantsApi {

	private final TenantMapper mapper;
	private final TenantRepository tenantRepository;
	private final GatewayRepository gatewayRepository;

	@Override
	public HttpResponse<List<TenantVO>> findTenants() {
		return HttpResponse.ok(mapper.toTenants(tenantRepository.findAllOrderByName()));
	}

	@Override
	public HttpResponse<TenantVO> findTenant(UUID tenantId) {
		var tenant = tenantRepository.findByTenantId(tenantId);
		if (tenant.isEmpty()) {
			throw new HttpStatusException(HttpStatus.NOT_FOUND, "Tenant not found.");
		}
		return HttpResponse.ok(mapper.toTenant(tenant.get()));
	}

	@Override
	public HttpResponse<TenantVO> createTenant(TenantCreateVO vo) {

		// check id/name for uniqueness

		var uniqueSingle = vo.getTenantId() == null
				? tenantRepository.existsByName(vo.getName())
				: tenantRepository.existsByNameOrTenantId(vo.getName(), vo.getTenantId());
		if (uniqueSingle) {
			throw new HttpStatusException(HttpStatus.CONFLICT, "Already exists.");
		}

		// create tenant
		var tenant = tenantRepository.save(new Tenant()
				.setName(vo.getName())
				.setEnabled(vo.getEnabled())
				.setTenantId(vo.getTenantId() == null ? UUID.randomUUID() : vo.getTenantId()));

		// return
		return HttpResponse.created(mapper.toTenant(tenant));
	}

	@Override
	public HttpResponse<TenantVO> updateTenant(UUID tenantId, TenantUpdateVO vo) {

		// get tenant from database

		var optionalTenant = tenantRepository.findByTenantId(tenantId);
		if (optionalTenant.isEmpty()) {
			throw new HttpStatusException(HttpStatus.NOT_FOUND, "Tenant not found.");
		}

		// update fields

		var changed = new AtomicBoolean(false);
		var tenant = optionalTenant.get();
		if (vo.getName() != null) {
			if (tenant.getName().equals(vo.getName())) {
				log.trace("Tenant {}: skip update of name because not changed.", tenant.getName());
			} else {
				Boolean existsByName = tenantRepository.existsByName(vo.getName());
				if (existsByName) {
					throw new HttpStatusException(HttpStatus.CONFLICT, "Already exists.");
				}
				log.info("Tenant {}: updated name to {}.", tenant.getName(), vo.getName());
				changed.set(true);
				tenant.setName(vo.getName());
			}
		}
		if (vo.getEnabled() != null) {
			if (tenant.getEnabled() == vo.getEnabled()) {
				log.trace("Skip update of enabled {} because not changed.", tenant.getEnabled());
			} else {
				log.info("Tenant {}: updated enabled to {}.", tenant.getName(), vo.getEnabled());
				changed.set(true);
				tenant.setEnabled(vo.getEnabled());
			}
		}

		// return updated

		if (changed.get()) {
			tenant = tenantRepository.update(tenant);
		}

		return HttpResponse.ok(mapper.toTenant(tenant));
	}

	@Override
	public HttpResponse<Object> deleteTenant(UUID tenantId) {
		var tenant = tenantRepository.findByTenantId(tenantId);
		if (tenant.isEmpty()) {
			throw new HttpStatusException(HttpStatus.NOT_FOUND, "Tenant not found.");
		}
		if (gatewayRepository.existsByTenant(tenant.get())) {
			throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Gateways exists.");
		}
		tenantRepository.delete(tenant.get());
		return HttpResponse.noContent();
	}
}
