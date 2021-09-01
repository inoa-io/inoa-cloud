package io.kokuwa.fleet.registry.hono.impl;

import java.net.HttpURLConnection;
import java.util.Optional;

import org.eclipse.hono.deviceregistry.service.device.AbstractDeviceManagementService;
import org.eclipse.hono.deviceregistry.service.device.DeviceKey;
import org.eclipse.hono.deviceregistry.util.DeviceRegistryUtils;
import org.eclipse.hono.service.management.Id;
import org.eclipse.hono.service.management.OperationResult;
import org.eclipse.hono.service.management.Result;
import org.eclipse.hono.service.management.device.Device;
import org.eclipse.hono.util.RegistrationConstants;
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
public class DeviceManagementServiceImpl extends AbstractDeviceManagementService {

	private final RestTemplate restTemplate;
	private final InoaProperties inoaProperties;
	private final TokenService tokenService;

	@Override
	protected Future<OperationResult<Id>> processCreateDevice(DeviceKey deviceKey, Device device, Span span) {
		log.info("DeviceManagementServiceImpl.processCreateDevice");
		return null;
	}

	@Override
	protected Future<OperationResult<Device>> processReadDevice(DeviceKey deviceKey, Span span) {
		log.info("DeviceManagementServiceImpl.processReadDevice");
		Future<OperationResult<Device>> future = Future.future();
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(tokenService.getAccessToken());
		headers.add("x-inoa-tenant", deviceKey.getTenantId());
		HttpEntity<?> request = new HttpEntity<>(null, headers);
		try {
			ResponseEntity<JsonNode> exchange = restTemplate.exchange(
					String.format("http://%s:%d/gateways/%s", inoaProperties.getGatewayRegistryHost(),
							inoaProperties.getGatewayRegistryPort(), deviceKey.getDeviceId()),
					HttpMethod.GET, request, JsonNode.class);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		Device device = new Device();
		device.setEnabled(true);
		/*
		 * future.complete(RegistrationResult.from(HttpURLConnection.HTTP_OK,
		 * convertDevice(deviceKey.getDeviceId(), device),
		 * DeviceRegistryUtils.getCacheDirective(180)));
		 */
		future.complete(OperationResult.ok(HttpURLConnection.HTTP_OK, device,
				Optional.ofNullable(DeviceRegistryUtils.getCacheDirective(180)), Optional.ofNullable("1")));
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
	protected Future<OperationResult<Id>> processUpdateDevice(DeviceKey deviceKey, Device device,
			Optional<String> optional, Span span) {
		log.info("DeviceManagementServiceImpl.processUpdateDevice");
		return null;
	}

	@Override
	protected Future<Result<Void>> processDeleteDevice(DeviceKey deviceKey, Optional<String> optional, Span span) {
		log.info("DeviceManagementServiceImpl.processDeleteDevice");
		return null;
	}
}
