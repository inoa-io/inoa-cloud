package io.inoa.fleet.registry.test;

import static io.inoa.fleet.registry.rest.HttpResponseAssertions.assert200;

import java.util.HashMap;
import java.util.Map;

import io.micronaut.http.HttpHeaderValues;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;

@Singleton
@RequiredArgsConstructor
public class KeycloakClient {

	@Inject
	@Client("keycloak")
	HttpClient keycloak;

	private final Map<String, String> userTokens = new HashMap<>();
	private String token;

	public String auth() {
		if (token == null) {
			var request = HttpRequest
					.POST("/realms/inoa/protocol/openid-connect/token", Map.of(
							"client_id", "gateway-registry-client",
							"client_secret", "changeMe",
							"grant_type", "client_credentials"))
					.contentType(MediaType.APPLICATION_FORM_URLENCODED);
			var response = assert200(() -> keycloak.toBlocking().exchange(request, Map.class));
			token = (String) response.getBody().orElseThrow().get("access_token");
		}
		return HttpHeaderValues.AUTHORIZATION_PREFIX_BEARER + " " + token;
	}

	public String auth(String username) {
		if (!userTokens.containsKey(username)) {
			var request = HttpRequest
					.POST("/realms/inoa/protocol/openid-connect/token", Map.of(
							"client_id", "inoa-ui",
							"client_secret", "changeMe",
							"username", username,
							"password", "password",
							"grant_type", "password"))
					.contentType(MediaType.APPLICATION_FORM_URLENCODED);
			var response = assert200(() -> keycloak.toBlocking().exchange(request, Map.class));
			userTokens.put(username, (String) response.getBody().orElseThrow().get("access_token"));
		}
		return HttpHeaderValues.AUTHORIZATION_PREFIX_BEARER + " " + userTokens.get(username);
	}
}
