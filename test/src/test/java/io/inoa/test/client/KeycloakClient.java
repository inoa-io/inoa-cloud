package io.inoa.test.client;

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

/**
 * Client for keycloak.
 *
 * @author Stephan Schnabel
 */
@Singleton
@RequiredArgsConstructor
public class KeycloakClient {

	@Inject
	@Client("keycloak")
	HttpClient keycloak;

	private final Map<String, Realm> realms = new HashMap<>();

	public Realm realmInoa() {
		return realm("inoa");
	}

	public Realm realm(String name) {
		return realms.computeIfAbsent(name, Realm::new);
	}

	private String accessToken(String realm, Map<String, String> requestBody) {
		var request = HttpRequest
				.POST("/realms/" + realm + "/protocol/openid-connect/token", requestBody)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED);
		var response = keycloak.toBlocking().exchange(request, Map.class);
		return (String) response.getBody().map(map -> map.get("access_token")).orElseThrow();
	}

	@RequiredArgsConstructor
	public class Realm {

		private final String name;
		private final Map<String, Map<String, String>> userTokens = new HashMap<>();
		private final Map<String, String> tokens = new HashMap<>();

		public String token(String clientId, String username) {
			return HttpHeaderValues.AUTHORIZATION_PREFIX_BEARER + " " + userTokens
					.computeIfAbsent(clientId, id -> new HashMap<>())
					.computeIfAbsent(username, u -> accessToken(name, Map.of(
							"client_id", clientId,
							"client_secret", "changeMe",
							"username", username,
							"password", "password",
							"grant_type", "password")));
		}

		public String token(String clientId) {
			return HttpHeaderValues.AUTHORIZATION_PREFIX_BEARER + " " + tokens
					.computeIfAbsent(clientId, id -> accessToken(name, Map.of(
							"client_id", clientId,
							"client_secret", "changeMe",
							"grant_type", "client_credentials")));
		}
	}
}
