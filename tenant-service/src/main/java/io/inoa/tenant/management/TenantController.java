package io.inoa.tenant.management;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.mapstruct.factory.Mappers;
import org.slf4j.MDC;

import io.inoa.tenant.domain.Tenant;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.exceptions.HttpStatusException;
import lombok.RequiredArgsConstructor;

/**
 * Controller for {@link TenantsApi}.
 *
 * @author Stephan Schnabel
 */
@Controller
@RequiredArgsConstructor
public class TenantController implements TenantsApi {

	private final ManagementMapper mapper = Mappers.getMapper(ManagementMapper.class);
	private final ManagementService service;

	@Override
	public HttpResponse<List<TenantVO>> findTenants() {
		return HttpResponse.ok(service.findTenants().stream()
				.sorted(Comparator.comparing(Tenant::getName))
				.map(mapper::toTenant)
				.collect(Collectors.toList()));
	}

	@Override
	public HttpResponse<TenantVO> findTenant(String tenantId) {
		MDC.put("tenantId", tenantId);
		return service.findTenant(tenantId)
				.map(mapper::toTenant)
				.map(HttpResponse::ok)
				.orElseGet(HttpResponse::notFound);
	}

	@Override
	public HttpResponse<TenantVO> createTenant(String tenantId, @Valid TenantCreateVO vo) {
		MDC.put("tenantId", tenantId);
		if (service.existsTenant(tenantId)) {
			throw new HttpStatusException(HttpStatus.CONFLICT, "Already exists.");
		}
		var tenant = service.createTenant(new Tenant()
				.setTenantId(tenantId)
				.setEnabled(vo.getEnabled())
				.setName(vo.getName()));
		return HttpResponse.created(mapper.toTenant(tenant));
	}

	@Override
	public HttpResponse<TenantVO> updateTenant(String tenantId, @Valid TenantUpdateVO vo) {
		MDC.put("tenantId", tenantId);
		return service.updateTenant(tenantId, vo)
				.map(mapper::toTenant)
				.map(HttpResponse::ok)
				.orElseGet(HttpResponse::notFound);
	}

	@Override
	public HttpResponse<Object> deleteTenant(String tenantId) {
		MDC.put("tenantId", tenantId);
		return service.deleteTenant(tenantId) ? HttpResponse.noContent() : HttpResponse.notFound();
	}
}
