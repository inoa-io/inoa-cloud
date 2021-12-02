package io.inoa.cnpm.tenant.management;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.Size;

import org.mapstruct.factory.Mappers;
import org.slf4j.MDC;

import io.inoa.cnpm.tenant.domain.Tenant;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.exceptions.HttpStatusException;
import lombok.RequiredArgsConstructor;

/**
 * Controller for {@link IssuersApi}.
 *
 * @author Stephan Schnabel
 */
@Controller
@RequiredArgsConstructor
public class IssuerController implements IssuersApi {

	private final ManagementMapper mapper = Mappers.getMapper(ManagementMapper.class);
	private final ManagementService service;

	@Override
	public HttpResponse<List<IssuerVO>> findIssuers(String tenantId) {
		try (var mdc = MDC.putCloseable("tenantId", tenantId)) {
			return HttpResponse.ok(getTenant(tenantId).getIssuers().stream()
					.map(mapper::toIssuer)
					.sorted(Comparator.comparing(IssuerVO::getName))
					.collect(Collectors.toList()));
		}
	}

	@Override
	public HttpResponse<IssuerVO> findIssuer(String tenantId, String name) {
		try (var mdc = MDC.putCloseable("tenantId", tenantId)) {
			return service.findIssuer(getTenant(tenantId), name)
					.map(mapper::toIssuer)
					.map(HttpResponse::ok)
					.orElseGet(HttpResponse::notFound);
		}
	}

	@Transactional
	@Override
	public HttpResponse<IssuerVO> createIssuer(String tenantId, @Size(max = 10) String name, @Valid IssuerCreateVO vo) {
		try (var mdc = MDC.putCloseable("tenantId", tenantId)) {
			return HttpResponse.created(mapper.toIssuer(service.createIssuer(getTenant(tenantId), name, vo)));
		}
	}

	@Transactional
	@Override
	public HttpResponse<IssuerVO> updateIssuer(String tenantId, @Size(max = 10) String name, @Valid IssuerUpdateVO vo) {
		try (var mdc = MDC.putCloseable("tenantId", tenantId)) {
			return service.updateIssuer(getTenant(tenantId), name, vo)
					.map(mapper::toIssuer)
					.map(HttpResponse::ok)
					.orElseGet(HttpResponse::notFound);
		}
	}

	@Transactional
	@Override
	public HttpResponse<Object> deleteIssuer(String tenantId, String name) {
		try (var mdc = MDC.putCloseable("tenantId", tenantId)) {
			return service.deleteIssuer(getTenant(tenantId), name) ? HttpResponse.noContent() : HttpResponse.notFound();
		}
	}

	private Tenant getTenant(String tenantId) {
		return service
				.findTenant(tenantId)
				.orElseThrow(() -> new HttpStatusException(HttpStatus.NOT_FOUND, "Tenant not found."));
	}
}
