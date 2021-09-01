package io.kokuwa.fleet.registry.hono.impl;

import java.net.HttpURLConnection;
import java.util.Set;

import org.eclipse.hono.deviceregistry.service.device.AbstractRegistrationService;
import org.eclipse.hono.deviceregistry.service.device.DeviceKey;
import org.eclipse.hono.deviceregistry.util.DeviceRegistryUtils;
import org.eclipse.hono.service.management.device.Device;
import org.eclipse.hono.util.RegistrationConstants;
import org.eclipse.hono.util.RegistrationResult;
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
public class RegistrationServiceImpl extends AbstractRegistrationService {

	private final RestTemplate restTemplate;
	private final InoaProperties inoaProperties;
	private final TokenService tokenService;

	@Override
	protected Future<RegistrationResult> getRegistrationInformation(DeviceKey deviceKey, Span span) {
		log.info("RegistrationServiceImpl.getRegistrationInformation");
		Future<RegistrationResult> future = Future.future();
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(tokenService.getAccessToken());
		headers.add("x-inoa-tenant", deviceKey.getTenantId());
		HttpEntity<?> request = new HttpEntity<>(null, headers);
		try {
			ResponseEntity<JsonNode> exchange = restTemplate.exchange(
					String.format("http://%s:%d/gateways/%s", inoaProperties.getGatewayRegistryHost(),
							inoaProperties.getGatewayRegistryPort(), deviceKey.getDeviceId()),
					HttpMethod.GET, request, JsonNode.class);

			Device device = new Device();
			device.setEnabled(true);
			future.complete(RegistrationResult.from(HttpURLConnection.HTTP_OK,
					convertDevice(deviceKey.getDeviceId(), device), DeviceRegistryUtils.getCacheDirective(180)));
		} catch (Exception e) {
			log.error(e.getMessage());
			future.complete(RegistrationResult.from(HttpURLConnection.HTTP_NOT_FOUND));
		}
		return future;
	}

	private JsonObject convertDevice(final String deviceId, final Device payload) {
		if (payload == null) {
			return null;
		}
		final JsonObject data = JsonObject.mapFrom(payload);
		return new JsonObject().put(RegistrationConstants.FIELD_PAYLOAD_DEVICE_ID, deviceId)
				.put(RegistrationConstants.FIELD_DATA, data);
	}

	@Override
	protected Future<Set<String>> processResolveGroupMembers(String s, Set<String> set, Span span) {
		log.info("RegistrationServiceImpl.processResolveGroupMembers");
		return null;
	}
}
