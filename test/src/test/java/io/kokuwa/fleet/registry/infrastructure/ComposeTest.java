package io.kokuwa.fleet.registry.infrastructure;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.validation.Validator;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.SignedJWT;

import io.kokuwa.fleet.registry.rest.HttpResponseAssertions;
import io.micronaut.http.HttpHeaderValues;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import lombok.SneakyThrows;

/**
 * Base for all integration tests.
 *
 * @author Stephan Schnabel
 */
@MicronautTest
@TestMethodOrder(MethodOrderer.MethodName.class)
public abstract class ComposeTest {

	public static final String GRANT_TYPE = "urn:ietf:params:oauth:grant-type:jwt-bearer";
	public static final String ADMIN = "admin";
	public static final String USER_1 = "user1";
	public static final String USER_2 = "user2";

	@Inject
	Validator validator;
	@Inject
	@Client("keycloak")
	RxHttpClient keycloak;

	// security

	public String auth() {
		return auth(ADMIN);
	}

	public String auth(String username) {
		var request = HttpRequest
				.POST("/realms/kokuwa/protocol/openid-connect/token", Map.of(
						"client_id", "gateway-registry-ui",
						"client_secret", "changeMe",
						"username", username,
						"password", "password",
						"grant_type", "password"))
				.contentType(MediaType.APPLICATION_FORM_URLENCODED);
		var response = assert200(() -> keycloak.toBlocking().exchange(request, Map.class));
		var token = (String) response.get("access_token");
		assertNotNull(token, "no accesstoken found: " + response);
		return HttpHeaderValues.AUTHORIZATION_PREFIX_BEARER + " " + token;
	}

	@SneakyThrows
	private String authHmac(UUID gatewayId, String secret) {
		var now = Instant.now();
		var claims = new com.nimbusds.jwt.JWTClaimsSet.Builder()
				.audience("registry")
				.jwtID(UUID.randomUUID().toString())
				.issuer(gatewayId.toString())
				.issueTime(Date.from(now))
				.notBeforeTime(Date.from(now))
				.expirationTime(Date.from(now.plusSeconds(1)));
		var jwt = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claims.build());
		jwt.sign(new MACSigner(secret));
		return jwt.serialize();
	}

	// asserts

	public <T> T assert200(Supplier<HttpResponse<T>> executeable) {
		return assertValid(HttpResponseAssertions.assert200(executeable).body());
	}

	public <T> T assert201(Supplier<HttpResponse<T>> executeable) {
		return assertValid(HttpResponseAssertions.assert201(executeable).body());
	}

	private <T> T assertValid(T object) {
		if (object instanceof Iterable) {
			Iterable.class.cast(object).forEach(this::assertValid);
		} else {
			var violations = validator.validate(object);
			assertTrue(violations.isEmpty(), () -> "validation failed with:" + violations.stream()
					.map(v -> "\n\t" + v.getPropertyPath() + ": " + v.getMessage())
					.collect(Collectors.joining()));
		}
		return object;
	}
}
