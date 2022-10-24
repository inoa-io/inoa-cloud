package io.inoa.fleet.registry.hono.impl;

import java.net.HttpURLConnection;

import org.eclipse.hono.auth.HonoPasswordEncoder;
import org.eclipse.hono.auth.SpringBasedHonoPasswordEncoder;
import org.eclipse.hono.service.credentials.CredentialsService;
import org.eclipse.hono.util.CredentialsConstants;
import org.eclipse.hono.util.CredentialsResult;
import org.eclipse.hono.util.RequestResponseApiConstants;
import org.springframework.stereotype.Service;

import io.inoa.fleet.registry.hono.rest.RegistryClient;
import io.inoa.fleet.registry.hono.rest.RegistryProperties;
import io.opentracing.Span;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class CredentialsServiceImpl implements CredentialsService {

	private final HonoPasswordEncoder passwordEncoder = new SpringBasedHonoPasswordEncoder(10);
	private final RegistryClient registryClient;
	private final RegistryProperties properties;

	@Override
	public Future<CredentialsResult<JsonObject>> get(String tenantId, String type, String gatewayId,
			JsonObject clientContext, Span span) {
		log.info("get {}@{} {} - {}", gatewayId, tenantId, type, clientContext);
		return Future.succeededFuture(registryClient.findPassword(tenantId, gatewayId)
				.map(password -> new JsonObject().put(RequestResponseApiConstants.FIELD_ENABLED, true)
						.put(RequestResponseApiConstants.FIELD_PAYLOAD_DEVICE_ID, gatewayId)
						.put(CredentialsConstants.FIELD_SECRETS,
								new JsonArray().add(passwordEncoder.encode(new String(password.getValue()))))
						.put(CredentialsConstants.FIELD_TYPE, CredentialsConstants.SECRETS_TYPE_HASHED_PASSWORD)
						.put(CredentialsConstants.FIELD_AUTH_ID, gatewayId))
				.map(json -> CredentialsResult.from(HttpURLConnection.HTTP_OK, json, properties.getCredentialCache()))
				.orElseGet(() -> CredentialsResult.from(HttpURLConnection.HTTP_NOT_FOUND)));
	}

	@Override
	public Future<CredentialsResult<JsonObject>> get(String tenantId, String type, String authId, Span span) {
		return Future.failedFuture("Not implemented.");
	}
}
