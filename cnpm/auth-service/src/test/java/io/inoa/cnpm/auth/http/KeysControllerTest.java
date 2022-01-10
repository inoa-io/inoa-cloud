package io.inoa.cnpm.auth.http;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.text.ParseException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.nimbusds.jose.jwk.JWKSet;

import io.inoa.cnpm.auth.AbstractTest;
import io.inoa.cnpm.auth.service.TokenLocalService;
import io.micronaut.context.ApplicationContext;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.security.token.jwt.endpoints.KeysController;
import jakarta.inject.Inject;

/**
 * Test for {@link KeysController} and his configuration.
 */
@DisplayName("http: keys controller")
public class KeysControllerTest extends AbstractTest {

	@Inject
	@Client("/")
	HttpClient client;
	@Inject
	ApplicationContext c;
	@Inject
	TokenLocalService service;

	@DisplayName("keys are available")
	@Test
	void keys() throws ParseException {
		var request = HttpRequest.GET("/protocol/openid-connect/certs").accept(MediaType.APPLICATION_JSON);
		var response = client.toBlocking().exchange(request, String.class);
		assertEquals(io.micronaut.http.HttpStatus.OK, response.getStatus(), "status");
		var jwkSet = JWKSet.parse(response.body());
		assertEquals(1, jwkSet.getKeys().size(), "keys");
		var actualKey = jwkSet.getKeys().iterator().next();
		var expectedKey = service.retrieveJsonWebKeys().iterator().next().toPublicJWK();
		assertEquals(expectedKey, actualKey, "key");
	}
}
