package io.inoa.fleet.registry.hono.impl;

import java.net.HttpURLConnection;
import java.util.Map;

import org.eclipse.hono.service.management.device.Device;
import org.eclipse.hono.service.registration.RegistrationService;
import org.eclipse.hono.util.RegistrationConstants;
import org.eclipse.hono.util.RegistrationResult;
import org.eclipse.hono.util.RequestResponseApiConstants;
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
		return Future.succeededFuture(registryClient.findGateway(tenantId, deviceId)
				.map(device -> new JsonObject()
						.put(RequestResponseApiConstants.FIELD_PAYLOAD_DEVICE_ID, deviceId)
						.put(RegistrationConstants.FIELD_DATA, JsonObject.mapFrom(new Device()
								.setEnabled(device.getEnabled())
								// TODO move the defaults to the database
								// it is only a workaround for now
								.setDefaults(Map.of("gatewayName", device.getName(), "gatewayType", "satellite")))))
				.map(json -> RegistrationResult.from(HttpURLConnection.HTTP_OK, json, properties.getGatewayCache()))
				.orElseGet(() -> RegistrationResult.from(HttpURLConnection.HTTP_NOT_FOUND)));
	}

	@Override
	public Future<RegistrationResult> assertRegistration(String tenantId, String deviceId, String gatewayId) {
		return Future.failedFuture("Not implemented.");
	}
}
