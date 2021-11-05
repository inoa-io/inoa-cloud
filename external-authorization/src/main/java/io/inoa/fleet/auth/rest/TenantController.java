package io.inoa.fleet.auth.rest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.validation.Valid;

import io.inoa.fleet.auth.domain.Tenant;
import io.inoa.fleet.auth.domain.TenantRepository;
import io.inoa.fleet.auth.domain.TenantUser;
import io.inoa.fleet.auth.domain.TenantUserRepository;
import io.inoa.fleet.auth.domain.User;
import io.inoa.fleet.auth.domain.UserRepository;
import io.inoa.fleet.auth.mapper.TenantMapper;
import io.inoa.fleet.auth.rest.management.TenantCreateVO;
import io.inoa.fleet.auth.rest.management.TenantUpdateVO;
import io.inoa.fleet.auth.rest.management.TenantVO;
import io.inoa.fleet.auth.rest.management.TenantsApi;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.exceptions.HttpStatusException;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.utils.SecurityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class TenantController implements TenantsApi {

	private final TenantMapper mapper;
	private final TenantRepository tenantRepository;
	private final TenantUserRepository tenantUserRepository;
	private final UserRepository userRepository;
	private final SecurityService securityService;

	@Override
	public HttpResponse<TenantVO> createTenant(@Valid TenantCreateVO vo) {
		// check id/name for uniqueness

		var existsTenant = tenantRepository.existsByTenantId(vo.getTenantId());
		if (existsTenant) {
			throw new HttpStatusException(HttpStatus.CONFLICT, "Already exists.");
		}
		String email = getEmailForCurrentUser();
		Optional<User> optionalUser = userRepository.findByEmail(email);
		User user;
		if (optionalUser.isEmpty()) {
			user = userRepository.save(new User().setUserId(UUID.randomUUID()).setEmail(email));
		} else {
			user = optionalUser.get();
		}

		// create tenant
		var tenant = tenantRepository
				.save(new Tenant().setName(vo.getName()).setEnabled(vo.getEnabled()).setTenantId(vo.getTenantId()));

		// this relation should not be existing
		tenantUserRepository.save(new TenantUser().setTenant(tenant).setUser(user));

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
		String email = getEmailForCurrentUser();
		Optional<User> optionalUser = userRepository.findByEmail(email);
		if (optionalUser.isEmpty()) {
			return HttpResponse.ok(List.of());
		}
		List<Tenant> tenants = tenantUserRepository.findTenantByUser(optionalUser.get());
		return HttpResponse.ok(mapper.toTenants(tenants));
	}

	@Override
	public HttpResponse<TenantVO> updateTenant(String tenantId, @Valid TenantUpdateVO vo) {
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
				tenant.setEnabled(vo.getEnabled());
			}
		}
		// return updated

		if (changed.get()) {
			tenant = tenantRepository.update(tenant);
		}
		return HttpResponse.ok(mapper.toTenant(tenant));
	}

	private String getEmailForCurrentUser() {
		Optional<Authentication> authentication = securityService.getAuthentication();
		if (authentication.isEmpty()) {
			throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "Not authorized.");
		}
		return authentication.get().getAttributes().get("email").toString();
	}
}
