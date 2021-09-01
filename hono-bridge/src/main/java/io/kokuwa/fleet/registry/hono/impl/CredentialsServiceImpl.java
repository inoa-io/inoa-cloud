package io.kokuwa.fleet.registry.hono.impl;

import java.net.HttpURLConnection;
import java.util.List;
import java.util.Optional;

import org.eclipse.hono.auth.HonoPasswordEncoder;
import org.eclipse.hono.deviceregistry.service.credentials.AbstractCredentialsService;
import org.eclipse.hono.deviceregistry.service.credentials.CredentialKey;
import org.eclipse.hono.deviceregistry.service.tenant.TenantKey;
import org.eclipse.hono.deviceregistry.util.DeviceRegistryUtils;
import org.eclipse.hono.util.CredentialsConstants;
import org.eclipse.hono.util.CredentialsResult;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.kokuwa.fleet.registry.hono.TokenService;
import io.kokuwa.fleet.registry.hono.config.InoaProperties;
import io.opentracing.Span;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class CredentialsServiceImpl extends AbstractCredentialsService {

	private final RestTemplate restTemplate;
	private final InoaProperties inoaProperties;
	private final TokenService tokenService;
	private final HonoPasswordEncoder passwordEncoder;

	@Data
	private static class Secret {
		@JsonProperty("secret_id")
		private String secretId;
		private byte[] password;
	}

	@Data
	private static class GatewayCredentials {
		private String type;
		@JsonProperty("credential_id")
		private String credentialId;
		private List<Secret> secrets;
	}

	@Override
	protected Future<CredentialsResult<JsonObject>> processGet(TenantKey tenantKey, CredentialKey credentialKey,
			JsonObject jsonObject, Span span) {
		log.info("CredentialsServiceImpl.processGet");
		Future<CredentialsResult<JsonObject>> future = Future.future();
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(tokenService.getAccessToken());
		headers.add("x-inoa-tenant", tenantKey.getTenantId());
		HttpEntity<?> request = new HttpEntity<>(null, headers);

		JsonObject credential = new JsonObject();
		try {
			ResponseEntity<List<GatewayCredentials>> exchange = restTemplate.exchange(
					String.format("http://%s:%d/gateways/%s/credentials", inoaProperties.getGatewayRegistryHost(),
							inoaProperties.getGatewayRegistryPort(), credentialKey.getAuthId()),
					HttpMethod.GET, request, new ParameterizedTypeReference<>() {

					});
			if (exchange.getBody() != null) {
				Optional<GatewayCredentials> password = exchange.getBody().stream()
						.filter(i -> i.getType().equals("password")).findFirst();
				if (password.isPresent()) {
					Secret remoteSecret = password.get().getSecrets().get(0);

					ResponseEntity<Secret> secretResponseEntity = restTemplate
							.exchange(
									String.format("http://%s:%d/gateways/%s/credentials/%s/secrets/%s",
											inoaProperties.getGatewayRegistryHost(),
											inoaProperties.getGatewayRegistryPort(), credentialKey.getAuthId(),
											password.get().getCredentialId(), remoteSecret.getSecretId()),
									HttpMethod.GET, request, Secret.class);
					if (secretResponseEntity.getBody() != null) {
						JsonArray secrets = new JsonArray();
						JsonObject secret = new JsonObject();
						secret.put(CredentialsConstants.FIELD_SECRETS_HASH_FUNCTION,
								CredentialsConstants.HASH_FUNCTION_BCRYPT);
						log.info(new String(secretResponseEntity.getBody().getPassword()));
						secrets.add(passwordEncoder.encode(new String(secretResponseEntity.getBody().getPassword())));

						credential.put(CredentialsConstants.FIELD_SECRETS, secrets);
						credential.put(CredentialsConstants.FIELD_PAYLOAD_DEVICE_ID, credentialKey.getAuthId());
						credential.put(CredentialsConstants.FIELD_TYPE,
								CredentialsConstants.SECRETS_TYPE_HASHED_PASSWORD);
						credential.put(CredentialsConstants.FIELD_ENABLED, true);
						credential.put(CredentialsConstants.FIELD_AUTH_ID, credentialKey.getAuthId());
					}
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		log.info(credential.toString());
		future.complete(CredentialsResult.from(HttpURLConnection.HTTP_OK, credential.copy(),
				DeviceRegistryUtils.getCacheDirective(180)));
		return future;
	}
}
