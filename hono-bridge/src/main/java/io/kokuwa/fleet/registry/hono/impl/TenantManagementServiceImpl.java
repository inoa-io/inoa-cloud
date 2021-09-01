package io.kokuwa.fleet.registry.hono.impl;

import java.net.HttpURLConnection;
import java.util.Optional;

import org.eclipse.hono.deviceregistry.service.tenant.AbstractTenantManagementService;
import org.eclipse.hono.service.management.Id;
import org.eclipse.hono.service.management.OperationResult;
import org.eclipse.hono.service.management.Result;
import org.eclipse.hono.service.management.tenant.Tenant;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import io.kokuwa.fleet.registry.hono.TokenService;
import io.kokuwa.fleet.registry.hono.config.InoaProperties;
import io.opentracing.Span;
import io.vertx.core.Future;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class TenantManagementServiceImpl extends AbstractTenantManagementService {

	private final RestTemplate restTemplate;
	private final InoaProperties inoaProperties;
	private final TokenService tokenService;

	@Override
	protected Future<OperationResult<Id>> processCreateTenant(final String tenantId, final Tenant tenantObj,
			final Span span) {
		log.info("TenantManagementServiceImpl.processCreateTenant");
		return null;
	}

	@Override
	protected Future<OperationResult<Void>> processUpdateTenant(final String tenantId, final Tenant tenantObj,
			final Optional<String> resourceVersion, final Span span) {
		log.info("TenantManagementServiceImpl.processUpdateTenant");
		return null;
	}

	@Override
	public Future<OperationResult<Tenant>> readTenant(final String tenantId, final Span span) {
		log.info("TenantManagementServiceImpl.readTenant");
		Future<OperationResult<Tenant>> future = Future.future();
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(tokenService.getAccessToken());
		HttpEntity<?> request = new HttpEntity<>(null, headers);
		try {
			ResponseEntity<String> exchange = restTemplate
					.exchange(
							String.format("http://%s:%d/tenants/%s", inoaProperties.getGatewayRegistryHost(),
									inoaProperties.getGatewayRegistryPort(), tenantId),
							HttpMethod.GET, request, String.class);
			future.complete(
					OperationResult.ok(HttpURLConnection.HTTP_OK, new Tenant(), Optional.empty(), Optional.of("1")));
		} catch (Exception e) {
			log.error(e.getMessage());
			future.complete(OperationResult.empty(HttpURLConnection.HTTP_NOT_FOUND));
		}
		return future;
	}

	@Override
	public Future<Result<Void>> deleteTenant(final String tenantId, final Optional<String> resourceVersion,
			final Span span) {
		log.info("TenantManagementServiceImpl.deleteTenant");
		return null;
	}
}
