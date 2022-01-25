package io.inoa.fleet.registry.rest.management;

import io.inoa.fleet.registry.domain.TenantRepository;
import io.inoa.fleet.registry.rest.mapper.TenantMapper;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.exceptions.HttpStatusException;
import lombok.RequiredArgsConstructor;

/**
 * Implementation of {@link TenantsApi}.
 *
 * @author Stephan Schnabel
 */
@Controller
@RequiredArgsConstructor
public class TenantsController implements TenantsApi {

	private final Security security;
	private final TenantMapper mapper;
	private final TenantRepository tenantRepository;

	@Override
	public HttpResponse<TenantVO> findTenant(String tenantId) {
		if (!tenantId.equals(security.getTenantId())) {
			throw new HttpStatusException(HttpStatus.NOT_FOUND, "Tenant not found.");
		}
		var tenant = tenantRepository.findByTenantIdAndDeletedIsNull(tenantId);
		if (tenant.isEmpty()) {
			throw new HttpStatusException(HttpStatus.NOT_FOUND, "Tenant not found.");
		}
		return HttpResponse.ok(mapper.toTenant(tenant.get()));
	}
}
