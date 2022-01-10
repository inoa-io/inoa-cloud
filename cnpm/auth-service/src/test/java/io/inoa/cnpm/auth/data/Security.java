package io.inoa.cnpm.auth.data;

import java.net.URL;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import io.micronaut.runtime.server.EmbeddedServer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

/**
 * Test controller with multiple jwks endpoints.
 */
@Controller
@Getter
@RequiredArgsConstructor
public class Security {

	private final Map<String, JWK> jwks = Map.of("0", jwk("0"), "1", jwk("1"));
	private final EmbeddedServer server;

	@Get("/endpoints/test/{id}/protocol/openid-connect/certs")
	@Produces(MediaType.APPLICATION_JSON)
	Map<String, Object> jwkSet(String id) {
		return new JWKSet(jwks.getOrDefault(id, jwk("null"))).toJSONObject(true);
	}

	@SneakyThrows
	public URL getIssuer(String id) {
		return new URL("http://localhost:" + server.getPort() + "/endpoints/test/" + id);
	}

	public SignedJWT jwt(String id, Consumer<JWTClaimsSet.Builder> claimsModifier) {
		var claims = new JWTClaimsSet.Builder()
				.subject(UUID.randomUUID().toString())
				.expirationTime(Date.from(Instant.now().plusSeconds(60)))
				.issuer(getIssuer(id).toString());
		claimsModifier.accept(claims);
		return sign(id, claims.build());
	}

	// signing

	@SneakyThrows
	public SignedJWT sign(String id, JWTClaimsSet claims) {
		var jwk = jwks.getOrDefault(id, jwk(id));
		var jwtHeader = new JWSHeader.Builder(JWSAlgorithm.RS256)
				.keyID(jwk.getKeyID())
				.type(JOSEObjectType.JWT)
				.build();
		var jwt = new SignedJWT(jwtHeader, claims);
		var signer = new RSASSASigner(jwk.toRSAKey());
		jwt.sign(signer);
		return jwt;
	}

	@SneakyThrows
	private JWK jwk(String id) {
		var pair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
		return new RSAKey.Builder((RSAPublicKey) pair.getPublic())
				.privateKey((RSAPrivateKey) pair.getPrivate())
				.keyUse(KeyUse.SIGNATURE)
				.keyID(id + "_" + UUID.randomUUID().toString())
				.build();
	}

}
