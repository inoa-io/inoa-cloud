package io.kokuwa.fleet.registry.hono.impl;

import java.net.HttpURLConnection;

import javax.security.auth.x500.X500Principal;

import org.eclipse.hono.deviceregistry.service.tenant.AbstractTenantService;
import org.eclipse.hono.deviceregistry.util.DeviceRegistryUtils;
import org.eclipse.hono.service.management.tenant.Tenant;
import org.eclipse.hono.util.TenantResult;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;

import io.kokuwa.fleet.registry.hono.TokenService;
import io.kokuwa.fleet.registry.hono.config.InoaProperties;
import io.opentracing.Span;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class TenantServiceImpl extends AbstractTenantService {

	private final RestTemplate restTemplate;
	private final InoaProperties inoaProperties;
	private final TokenService tokenService;

	@Override
	public Future<TenantResult<JsonObject>> get(final String tenantId, final Span span) {
		log.info("TenantServiceImpl.get");
		Future<TenantResult<JsonObject>> future = Future.future();
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(tokenService.getAccessToken());
		HttpEntity<?> request = new HttpEntity<>(null, headers);
		try {
			ResponseEntity<JsonNode> exchange = restTemplate.exchange(
					String.format("http://%s:%d/tenants/%s", inoaProperties.getGatewayRegistryHost(),
							inoaProperties.getGatewayRegistryPort(), tenantId),
					HttpMethod.GET, request, JsonNode.class);

			Tenant tenant = new Tenant();
			tenant.setEnabled(true); // exchange.getBody().get("enabled").asBoolean());
			future.complete(TenantResult.from(HttpURLConnection.HTTP_OK,
					DeviceRegistryUtils.convertTenant(tenantId, tenant, true),
					DeviceRegistryUtils.getCacheDirective(180)));
		} catch (Exception e) {
			log.error(e.getMessage());
			future.complete(TenantResult.from(HttpURLConnection.HTTP_NOT_FOUND));
		}

		return future;
	}

	@Override
	public Future<TenantResult<JsonObject>> get(final X500Principal subjectDn, final Span span) {
		log.info("TenantServiceImpl.get");
		return null;
	}
}
