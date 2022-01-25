package io.inoa.test.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.SneakyThrows;

@Singleton
public class AuthClient {

	@Inject
	@Client("auth")
	HttpClient client;

	private JWKSet jwkSet;

	@SneakyThrows
	public JWKSet jwk() {
		if (jwkSet == null) {
			var request = HttpRequest.GET("/protocol/openid-connect/certs").accept(MediaType.APPLICATION_JSON);
			var response = client.toBlocking().exchange(request, String.class);
			assertEquals(io.micronaut.http.HttpStatus.OK, response.getStatus(), "status");
			jwkSet = JWKSet.parse(response.body());
		}
		return jwkSet;
	}

	@SneakyThrows
	public JWTClaimsSet parse(String token) {
		var jwt = SignedJWT.parse(token.startsWith("Bearer ") ? token.substring(7) : token);
		var key = jwk().getKeyByKeyId(jwt.getHeader().getKeyID());
		assertNotNull(key, "keyId not found");
		if (!jwt.verify(new RSASSAVerifier(key.toRSAKey()))) {
			fail("Token not valid.");
		}
		return jwt.getJWTClaimsSet();
	}
}
