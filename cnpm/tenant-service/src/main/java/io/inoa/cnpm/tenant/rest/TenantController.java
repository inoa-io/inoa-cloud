package io.inoa.cnpm.tenant.rest;

import java.util.List;
import java.util.Objects;

import javax.transaction.Transactional;
import javax.validation.Valid;

import io.inoa.cnpm.tenant.domain.Assignment;
import io.inoa.cnpm.tenant.domain.Issuer;
import io.inoa.cnpm.tenant.domain.Tenant;
import io.inoa.cnpm.tenant.messaging.CloudEventSubjectVO;
import io.micronaut.data.model.Sort;
import io.micronaut.data.model.Sort.Order;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.exceptions.HttpStatusException;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller for {@link TenantsApi}.
 */
@Controller
@Slf4j
public class TenantController extends RestController implements TenantsApi {

	@Override
	public HttpResponse<List<TenantPageEntryVO>> findTenants() {
		var tenants = assignmentRepository.findTenantByUserEmail(getUserEmail(), Sort.of(Order.asc("tenant.name")));
		return HttpResponse.ok(mapper.toTenants(tenants));
	}

	@Override
	public HttpResponse<TenantVO> findTenant(String tenantId) {
		var tenant = isService() ? getTenantAsService(tenantId) : getTenant(tenantId);
		return HttpResponse.ok(toTenant(tenant));
	}

	@Transactional
	@Override
	public HttpResponse<TenantVO> createTenant(String tenantId, @Valid TenantCreateVO vo) {

		if (tenantRepository.existsByTenantId(tenantId)) {
			throw new HttpStatusException(HttpStatus.CONFLICT, "Already exists.");
		}

		var tenant = new Tenant()
				.setTenantId(tenantId)
				.setEnabled(vo.getEnabled())
				.setName(vo.getName());
		var issuer = new Issuer()
				.setTenant(tenant)
				.setName(properties.getDefaultIssuer().getName())
				.setUrl(properties.getDefaultIssuer().getUrl())
				.setCacheDuration(properties.getDefaultIssuer().getCacheDuration())
				.setServices(properties.getDefaultIssuer().getServices());
		tenant.setIssuers(List.of(issuer));
		var email = getUserEmail();
		var user = userRepository.findByEmail(email).orElseGet(() -> userRepository.save(email));
		var assignment = new Assignment().setTenant(tenant).setUser(user).setRole(UserRoleVO.ADMIN);

		tenantRepository.save(tenant);
		issuerRepository.save(issuer);
		issuer.getServices().forEach(svc -> issuerRepository.addService(issuer.getId(), svc));
		assignmentRepository.save(assignment);
		messaging.send(tenant, CloudEventSubjectVO.CREATE);
		messaging.send(assignment, CloudEventSubjectVO.CREATE);

		log.info("Tenant {} created: {}", tenant.getTenantId(), tenant);

		return HttpResponse.created(toTenant(tenant));
	}

	@Transactional
	@Override
	public HttpResponse<TenantVO> updateTenant(String tenantId, @Valid TenantUpdateVO vo) {
		var tenant = getTenantAsAdmin(tenantId);
		var changed = false;

		var oldName = tenant.getName();
		var newName = vo.getName();
		if (newName != null) {
			if (Objects.equals(oldName, newName)) {
				log.trace("Tenant {}: skip update of name {} because not changed", tenantId, newName);
			} else {
				log.info("Tenant {}: updated name from {} to {}", tenantId, oldName, newName);
				changed = true;
				tenant.setName(newName);
			}
		}

		var oldEnabled = tenant.getEnabled();
		var newEnabled = vo.getEnabled();
		if (newEnabled != null) {
			if (Objects.equals(oldEnabled, newEnabled)) {
				log.trace("Tenant {}: skip update of enabled {} because not changed", tenantId, newEnabled);
			} else {
				log.info("Tenant {}: updated enabled to {}", tenantId, newEnabled);
				changed = true;
				tenant.setEnabled(newEnabled);
			}
		}

		if (changed) {
			tenantRepository.update(tenant);
			messaging.send(tenant, CloudEventSubjectVO.UPDATE);
		}

		return HttpResponse.ok(toTenant(tenant));
	}

	@Transactional
	@Override
	public HttpResponse<Object> deleteTenant(String tenantId) {
		var tenant = getTenantAsAdmin(tenantId);
		tenantRepository.deleteByTenantId(tenantId);
		messaging.send(tenant, CloudEventSubjectVO.DELETE);
		log.info("Tenant {} deleted:  {}", tenantId, tenant);
		return HttpResponse.noContent();
	}

	private TenantVO toTenant(Tenant tenant) {
		var issuers = issuerRepository.findByTenantOrderByName(tenant);
		issuers.forEach(issuer -> issuer.setServices(issuerRepository.findServices(issuer.getId())));
		return mapper.toTenant(tenant.setIssuers(issuers));
	}
}
