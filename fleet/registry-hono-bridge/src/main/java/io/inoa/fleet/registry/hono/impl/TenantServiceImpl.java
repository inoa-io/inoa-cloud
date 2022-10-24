package io.inoa.fleet.registry.hono.impl;

import java.net.HttpURLConnection;
import java.util.Map;

import javax.security.auth.x500.X500Principal;

import org.eclipse.hono.deviceregistry.util.DeviceRegistryUtils;
import org.eclipse.hono.service.management.tenant.Tenant;
import org.eclipse.hono.service.tenant.TenantService;
import org.eclipse.hono.util.TenantResult;
import org.springframework.stereotype.Service;

import io.inoa.fleet.registry.hono.rest.RegistryProperties;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class TenantServiceImpl implements TenantService {

	private final RegistryProperties properties;

	@Override
	public Future<TenantResult<JsonObject>> get(String tenantId) {
		log.info("get {}", tenantId);
		if (tenantId.equals("inoa")) {
			return Future
					.succeededFuture(DeviceRegistryUtils.convertTenant("inoa",
							new Tenant().setEnabled(true).setDefaults(Map.of("tenant_id", "ina"))))
					.map(json -> TenantResult.from(HttpURLConnection.HTTP_OK, json, properties.getTenantCache()));
		}
		return Future.succeededFuture(TenantResult.from(HttpURLConnection.HTTP_NOT_FOUND));
	}

	@Override
	public Future<TenantResult<JsonObject>> get(X500Principal subjectDn) {
		return Future.failedFuture("Not implemented.");
	}
}
