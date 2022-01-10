package io.inoa.cnpm.tenant.rest;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.Size;

import io.inoa.cnpm.tenant.domain.Issuer;
import io.inoa.cnpm.tenant.domain.IssuerService;
import io.inoa.cnpm.tenant.domain.Tenant;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.exceptions.HttpStatusException;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller for {@link IssuersApi}.
 */
@Controller
@Slf4j
public class IssuerController extends RestController implements IssuersApi {

	@Override
	public HttpResponse<List<IssuerVO>> findIssuers(String tenantId) {
		var issuers = issuerRepository.findByTenantOrderByName(getTenant(tenantId));
		issuers.forEach(issuer -> issuer.setServices(issuerRepository.findServices(issuer.getId())));
		return HttpResponse.ok(mapper.toIssuers(issuers));
	}

	@Override
	public HttpResponse<IssuerVO> findIssuer(String tenantId, String name) {
		return HttpResponse.ok(mapper.toIssuer(getIssuer(getTenant(tenantId), name)));
	}

	@Transactional
	@Override
	public HttpResponse<IssuerVO> createIssuer(String tenantId, @Size(max = 10) String name, @Valid IssuerCreateVO vo) {
		var tenant = getTenantAsAdmin(tenantId);

		var issuers = issuerRepository.findByTenantOrderByName(tenant);
		if (issuers.stream().anyMatch(i -> i.getName().equals(name))) {
			throw new HttpStatusException(HttpStatus.CONFLICT, "Name already exists.");
		}
		if (issuers.stream().anyMatch(i -> i.getUrl().equals(vo.getUrl()))) {
			throw new HttpStatusException(HttpStatus.CONFLICT, "URL already exists.");
		}

		var issuer = issuerRepository.save(new Issuer()
				.setTenant(tenant)
				.setName(name)
				.setUrl(vo.getUrl())
				.setCacheDuration(vo.getCacheDuration() == null
						? properties.getDefaultIssuer().getCacheDuration()
						: vo.getCacheDuration())
				.setServices(vo.getServices() == null ? Set.of() : vo.getServices()));
		issuer.getServiceObjects().forEach(o -> issuerRepository.addService(o.getIssuer().getId(), o.getName()));
		log.info("Tenant {} issuer created: {}", tenant.getTenantId(), issuer);

		return HttpResponse.created(mapper.toIssuer(issuer));
	}

	@Transactional
	@Override
	public HttpResponse<IssuerVO> updateIssuer(String tenantId, @Size(max = 10) String name, @Valid IssuerUpdateVO vo) {
		var tenant = getTenantAsAdmin(tenantId);
		var issuer = getIssuer(tenant, name);
		var issuers = issuerRepository.findByTenantOrderByName(tenant);
		var changed = false;

		var oldUrl = issuer.getUrl();
		var newUrl = vo.getUrl();
		if (newUrl != null) {
			if (Objects.equals(oldUrl, newUrl)) {
				log.trace("Tenant {} issuer {}: skip update of url {} because not changed", tenantId, name, newUrl);
			} else {
				if (issuers.stream().anyMatch(i -> i.getUrl().equals(newUrl))) {
					throw new HttpStatusException(HttpStatus.CONFLICT, "Url already exists.");
				}
				log.info("Tenant {} issuer {}: updated url from {} to {}", tenantId, name, oldUrl, newUrl);
				changed = true;
				issuer.setUrl(newUrl);
			}
		}

		var oldServices = issuer.getServices();
		var newServices = vo.getServices();
		if (newServices != null) {
			if (Objects.equals(oldServices, newServices)) {
				log.trace("Tenant {} issuer {}: skip update of services {} because not changed",
						tenantId, name, newServices);
			} else {
				oldServices.stream()
						.filter(svc -> !newServices.contains(svc))
						.peek(svc -> log.info("Tenant {} issuer {}: removed service {}", tenantId, name, svc))
						.peek(svc -> issuerRepository.deleteService(issuer.getId(), svc))
						.forEach(svc -> issuer.getServiceObjects().removeIf(s -> s.getName().equals(svc)));
				newServices.stream()
						.filter(svc -> !oldServices.contains(svc))
						.peek(svc -> log.info("Tenant {} issuer {}: added service {}", tenantId, name, svc))
						.peek(svc -> issuerRepository.addService(issuer.getId(), svc))
						.forEach(svc -> issuer.getServiceObjects().add(new IssuerService(issuer, svc)));
				changed = true;
			}
		}

		var oldCache = issuer.getCacheDuration();
		var newCache = vo.getCacheDuration();
		if (newCache != null) {
			if (Objects.equals(oldCache, newCache)) {
				log.trace("Tenant {} issuer {}: skip update of cache {} because not changed", tenantId, name, newCache);
			} else {
				log.info("Tenant {} issuer {}: updated cache from {} to {}", tenantId, name, oldCache, newCache);
				changed = true;
				issuer.setCacheDuration(newCache);
			}
		}

		if (changed) {
			issuerRepository.update(issuer);
		}

		return HttpResponse.ok(mapper.toIssuer(issuer));
	}

	@Transactional
	@Override
	public HttpResponse<Object> deleteIssuer(String tenantId, String name) {
		issuerRepository.delete(getIssuer(getTenantAsAdmin(tenantId), name));
		log.info("Tenant {} issuer {} deleted.", tenantId, name);
		return HttpResponse.noContent();
	}

	private Issuer getIssuer(Tenant tenant, String name) {
		return issuerRepository
				.findByTenantAndName(tenant, name)
				.map(issuer -> issuer.setServices(issuerRepository.findServices(issuer.getId())))
				.orElseThrow(() -> new HttpStatusException(HttpStatus.NOT_FOUND, "Issuer not found."));
	}
}
