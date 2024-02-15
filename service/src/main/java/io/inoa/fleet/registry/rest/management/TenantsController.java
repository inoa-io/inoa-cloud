package io.inoa.fleet.registry.rest.management;

import java.time.Instant;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import io.inoa.fleet.registry.domain.Tenant;
import io.inoa.fleet.registry.domain.TenantRepository;
import io.inoa.fleet.registry.rest.mapper.TenantMapper;
import io.inoa.rest.TenantCreateVO;
import io.inoa.rest.TenantUpdateVO;
import io.inoa.rest.TenantVO;
import io.inoa.rest.TenantsApi;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.exceptions.HttpStatusException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class TenantsController implements TenantsApi {

	/**
	 * Name of the default tenant that is already present in the database and cannot
	 * be deleted. Reason is to have a tenant to which new gateways are configured
	 * at self registration.
	 */
	public static final String DEFAULT_TENANT_ID = "inoa";

	private final TenantRepository repository;
	private final TenantMapper mapper;

	@Override
	public HttpResponse<TenantVO> createTenant(TenantCreateVO tenantCreateVO) {
		if (Boolean.TRUE.equals(repository.existsByTenantId(tenantCreateVO.getTenantId()))) {
			throw new HttpStatusException(HttpStatus.CONFLICT, "Tenant already exists.");
		}
		// Check Id Pattern
		checkGatewayIdPattern(tenantCreateVO.getGatewayIdPattern());
		Tenant tenant = repository.save(new Tenant().setTenantId(tenantCreateVO.getTenantId())
				.setName(tenantCreateVO.getName()).setEnabled(tenantCreateVO.getEnabled())
				.setGatewayIdPattern(tenantCreateVO.getGatewayIdPattern()));

		log.info("Created tenant: {}", tenant);
		return HttpResponse.created(mapper.toTenant(tenant));
	}

	@Override
	public HttpResponse<TenantVO> updateTenant(@NonNull String tenantId, @NonNull TenantUpdateVO tenantUpdateVO) {
		var changed = false;
		var tenant = repository.findByTenantId(tenantId);

		if (tenant.isEmpty()) {
			throw new HttpStatusException(HttpStatus.NOT_FOUND, "Tenant does not exist.");
		}

		var updatedTenant = tenant.get();

		if (tenantUpdateVO.getName() != null) {
			if (updatedTenant.getName().equals(tenantUpdateVO.getName())) {
				log.trace("Tenant {}: skip update of name because not changed.", updatedTenant.getName());
			} else {
				log.info("Group {}: updated name to {}.", updatedTenant.getName(), tenantUpdateVO.getName());
				changed = true;
				updatedTenant.setName(tenantUpdateVO.getName());
			}
		}
		if (tenantUpdateVO.getEnabled() != null) {
			if (updatedTenant.getEnabled().equals(tenantUpdateVO.getEnabled())) {
				log.trace("Tenant {}: skip update of enabled flag because not changed.", updatedTenant.getName());
			} else {
				log.info("Group {}: updated enabled flag to {}.", updatedTenant.getName(), tenantUpdateVO.getEnabled());
				changed = true;
				updatedTenant.setEnabled(tenantUpdateVO.getEnabled());
			}
		}
		if (tenantUpdateVO.getGatewayIdPattern() != null) {
			if (updatedTenant.getGatewayIdPattern().equals(tenantUpdateVO.getGatewayIdPattern())) {
				log.trace("Tenant {}: skip update of gateway ID pattern because not changed.", updatedTenant.getName());
			} else {
				// Check Id Pattern
				checkGatewayIdPattern(tenantUpdateVO.getGatewayIdPattern());
				log.info("Group {}: updated gateway ID pattern to {}.", updatedTenant.getName(),
						tenantUpdateVO.getGatewayIdPattern());
				changed = true;
				updatedTenant.setGatewayIdPattern(tenantUpdateVO.getGatewayIdPattern());
			}
		}

		if (changed) {
			return HttpResponse.ok(mapper.toTenant(repository.update(updatedTenant)));
		}

		return HttpResponse.ok(mapper.toTenant(updatedTenant));
	}

	@Override
	public HttpResponse<Object> deleteTenant(@NonNull String tenantId) {
		var tenant = repository.findByTenantId(tenantId);

		if (tenant.isEmpty()) {
			throw new HttpStatusException(HttpStatus.NOT_FOUND, "Tenant does not exist.");
		}

		if (DEFAULT_TENANT_ID.equals(tenantId)) {
			throw new HttpStatusException(HttpStatus.BAD_REQUEST, "The default tenant cannot be deleted.");
		}

		var deletedTenant = tenant.get();
		deletedTenant.setDeleted(Instant.now());
		repository.update(deletedTenant);
		log.info("Tenant {} deleted.", deletedTenant.getName());
		return HttpResponse.noContent();
	}

	@Override
	public HttpResponse<TenantVO> findTenant(@NonNull String tenantId) {
		var tenant = repository.findByTenantIdAndDeletedIsNull(tenantId);
		if (tenant.isEmpty()) {
			throw new HttpStatusException(HttpStatus.NOT_FOUND, "Tenant does not exist.");
		}
		return HttpResponse.ok(mapper.toTenant(tenant.get()));
	}

	@Override
	public HttpResponse<List<TenantVO>> findTenants() {
		var tenants = repository.findByDeletedIsNullOrderByTenantId();
		if (tenants.isEmpty()) {
			throw new HttpStatusException(HttpStatus.NOT_FOUND, "Tenant does not exist.");
		}
		return HttpResponse.ok(mapper.toTenants(tenants));
	}

	private void checkGatewayIdPattern(String pattern) {
		try {
			Pattern.compile(pattern);
		} catch (PatternSyntaxException e) {
			log.error("Attempt to create tenant with invalid gateway ID pattern: {}", pattern);
			throw new HttpStatusException(HttpStatus.BAD_REQUEST,
					"Invalid pattern for gateway ID: " + e.getLocalizedMessage());
		}
	}
}
