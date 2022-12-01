package io.inoa.fleet.registry.hono.impl;

import java.net.HttpURLConnection;
import java.util.Map;

import org.eclipse.hono.service.registration.RegistrationService;
import org.eclipse.hono.util.RegistrationConstants;
import org.eclipse.hono.util.RegistrationResult;
import org.springframework.stereotype.Service;

import io.inoa.fleet.registry.hono.rest.RegistryClient;
import io.inoa.fleet.registry.hono.rest.RegistryProperties;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {

	private final RegistryClient registryClient;
	private final RegistryProperties properties;

	@Override
	public Future<RegistrationResult> assertRegistration(String tenantId, String deviceId) {
		log.info("assertRegistration {}@{}", deviceId, tenantId);
		return Future
				.succeededFuture(
						registryClient
								.findGateway(tenantId, deviceId).map(
										device -> new JsonObject()
												.put(RegistrationConstants.FIELD_PAYLOAD_DEVICE_ID, deviceId)
												.put(RegistrationConstants.FIELD_ENABLED, device.getEnabled())
												.put(RegistrationConstants.FIELD_PAYLOAD_DEFAULTS, Map.of("gatewayName",
														device.getGatewayId(), "gatewayType", "satellite")))
								.map(json -> {
									if (json.getBoolean(RegistrationConstants.FIELD_ENABLED, Boolean.TRUE)) {
										return RegistrationResult.from(HttpURLConnection.HTTP_OK, json,
												properties.getGatewayCache());
									} else {
										return RegistrationResult.from(HttpURLConnection.HTTP_NOT_FOUND);
									}
								}).orElseGet(() -> RegistrationResult.from(HttpURLConnection.HTTP_NOT_FOUND)));
	}

	@Override
	public Future<RegistrationResult> assertRegistration(String tenantId, String deviceId, String gatewayId) {
		return Future.failedFuture("Not implemented.");
	}
}
