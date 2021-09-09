package io.inoa.fleet.registry.infrastructure;

import static io.inoa.fleet.registry.rest.HttpResponseAssertions.assert200;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.text.ParseException;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import io.inoa.fleet.registry.rest.gateway.AuthApiClient;
import io.micronaut.http.HttpHeaderValues;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import lombok.SneakyThrows;

/**
 * Client for authentication of user and gateways.
 *
 * @author Stephan Schnabel
 */
public class Auth {

	public static final String GRANT_TYPE = "urn:ietf:params:oauth:grant-type:jwt-bearer";

	public static final String ADMIN = "admin";
	public static final String USER_TENANT_A = "userFromTenantA";
	public static final String USER_TENANT_B = "userFromTenantB";

	@Inject
	@Client("keycloak")
	RxHttpClient keycloak;
	@Inject
	AuthApiClient authClient;

	public String user(String username) {
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
		return HttpHeaderValues.AUTHORIZATION_PREFIX_BEARER + " " + token;
	}

	@SneakyThrows
	public String hmac(UUID gatewayId, String secret) {
		var now = Instant.now();
		var claims = new JWTClaimsSet.Builder()
				.audience("gateway-registry")
				.jwtID(UUID.randomUUID().toString())
				.issuer(gatewayId.toString())
				.issueTime(Date.from(now))
				.notBeforeTime(Date.from(now))
				.expirationTime(Date.from(now.plusSeconds(10)));
		var jwt = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claims.build());
		jwt.sign(new MACSigner(secret));
		return jwt.serialize();
	}

	public String hmacRegistryToken(UUID gatewayId, String secret) {
		var response = assert200(() -> authClient.getToken(GRANT_TYPE, hmac(gatewayId, secret))).body();
		return HttpHeaderValues.AUTHORIZATION_PREFIX_BEARER + " " + response.accessToken();
	}

	public JWKSet getKeys() throws ParseException {
		return JWKSet.parse(assert200(() -> authClient.getKeys()).getBody(Map.class).orElseThrow());
	}
}
