package io.inoa.client;

import static io.inoa.junit.HttpAssertions.assert200;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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

	Map<String, String> userTokens = new HashMap<>();

	public String admin() {
		return auth("admin");
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
			var body = assert200(
					() -> keycloak.toBlocking().exchange(request, Map.class),
					"failed to get token for " + username);
			var token = (String) body.get("access_token");
			assertNotNull(token, "failed to read token from response");

			userTokens.put(username, token);
		}
		return HttpHeaderValues.AUTHORIZATION_PREFIX_BEARER + " " + userTokens.get(username);
	}
}