package io.inoa.fleet.registry.test;

import static io.inoa.fleet.registry.rest.HttpResponseAssertions.assert200;
import static io.inoa.fleet.registry.rest.HttpResponseAssertions.assert204;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.nimbusds.jose.jwk.JWKSet;

import io.inoa.fleet.registry.infrastructure.ComposeTest;
import io.inoa.fleet.registry.infrastructure.TestAssertions;
import io.inoa.fleet.registry.rest.gateway.PropertiesApiClient;
import io.inoa.fleet.registry.rest.management.ConfigurationApiClient;
import io.inoa.fleet.registry.rest.management.ConfigurationSetVO;
import io.inoa.fleet.registry.rest.management.CredentialCreateVO;
import io.inoa.fleet.registry.rest.management.CredentialTypeVO;
import io.inoa.fleet.registry.rest.management.CredentialsApiClient;
import io.inoa.fleet.registry.rest.management.GatewayCreateVO;
import io.inoa.fleet.registry.rest.management.GatewayDetailVO;
import io.inoa.fleet.registry.rest.management.GatewaysApiClient;
import io.inoa.fleet.registry.rest.management.SecretCreatePSKVO;
import io.inoa.fleet.registry.rest.management.SecretCreatePasswordVO;
import io.micronaut.http.HttpHeaderValues;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@Singleton
@RequiredArgsConstructor
public class GatewayRegistryClient {

	@Inject
	@Client("keycloak")
	HttpClient keycloak;

	private final TestAssertions assertions;
	private final GatewaysApiClient gatewaysClient;
	private final CredentialsApiClient credentialsClient;
	private final ConfigurationApiClient configurationClient;
	private final AuthApiFixedClient authClient;
	private final PropertiesApiClient propertiesClient;
	private final io.inoa.fleet.registry.rest.gateway.ConfigurationApiClient configurationGatewayClient;

	private final Map<String, String> userTokens = new HashMap<>();
	private String userToken;

	// auth

	public GatewayRegistryClient withUser(String username) {
		if (!userTokens.containsKey(username)) {
			var request = HttpRequest
					.POST("/realms/inoa/protocol/openid-connect/token", Map.of(
							"client_id", "gateway-registry-ui",
							"client_secret", "changeMe",
							"username", username,
							"password", "password",
							"grant_type", "password"))
					.contentType(MediaType.APPLICATION_FORM_URLENCODED);
			var response = assert200(() -> keycloak.toBlocking().exchange(request, Map.class));
			var token = (String) response.getBody().orElseThrow().get("access_token");
			assertNotNull(token, "no access token found");
			userTokens.put(username, HttpHeaderValues.AUTHORIZATION_PREFIX_BEARER + " " + token);
		}
		userToken = userTokens.get(username);
		return this;
	}

	// convinience

	@SneakyThrows
	public JWKSet getJwkSet() {
		return JWKSet.parse(assert200(() -> authClient.getKeys()).getBody(Map.class).orElseThrow());
	}

	public void setConfiguration(String key, Object value) {
		assert204(() -> configurationClient.setConfiguration(userToken, key, new ConfigurationSetVO().setValue(value)));
	}

	public GatewayClient createGateway() {
		return createGateway("dev-" + UUID.randomUUID().toString().substring(0, 10), UUID.randomUUID().toString());
	}

	public GatewayClient createGateway(String name, String secret) {

		var vo = new GatewayCreateVO().setName(name);
		var gateway = assertions.assert201(() -> gatewaysClient.createGateway(userToken, vo));
		var gatewayId = gateway.getGatewayId();
		assertions.assert201(() -> credentialsClient.createCredential(userToken, gatewayId, new CredentialCreateVO()
				.setAuthId("registry")
				.setType(CredentialTypeVO.PSK)
				.setSecrets(List.of(new SecretCreatePSKVO().setSecret(secret.getBytes())))));
		assertions.assert201(() -> credentialsClient.createCredential(userToken, gatewayId, new CredentialCreateVO()
				.setAuthId("hono")
				.setType(CredentialTypeVO.PASSWORD)
				.setSecrets(List.of(new SecretCreatePasswordVO().setPassword(secret.getBytes())))));

		return toClient(gatewayId, secret);
	}

	public GatewayClient toClient(UUID gatewayId, String secret) {
		return new GatewayClient(
				authClient, propertiesClient, configurationGatewayClient,
				ComposeTest.TENANT_ID, gatewayId, secret);
	}

	public Optional<GatewayClient> toClient(String name, String secret) {
		return assertions.assert200(() -> gatewaysClient.findGateways(userToken))
				.stream()
				.filter(gateway -> gateway.getName().equals(name))
				.findAny()
				.map(gateway -> toClient(gateway.getGatewayId(), secret));
	}

	public GatewayDetailVO findGateway(GatewayClient gateway) {
		return findGateway(gateway.getGatewayId());
	}

	public GatewayDetailVO findGateway(UUID gatewayId) {
		return assertions.assert200(() -> gatewaysClient.findGateway(userToken, gatewayId));
	}
}
