package io.inoa.cnpm.tenant.test;

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

import io.inoa.cnpm.tenant.domain.User;
import io.micronaut.context.annotation.Value;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import lombok.Getter;
import lombok.SneakyThrows;

/**
 * Test controller with multiple jwks endpoints.
 *
 * @author Stephan Schnabel
 */
@Controller
@Getter
public class TestJwkController {

	private final Map<String, JWK> jwks = Map.of("1", jwk("1"), "2", jwk("2"));

	@Value("${micronaut.server.port}")
	int port;

	@Get("/endpoints/test/{id}/protocol/openid-connect/certs")
	@Produces(MediaType.APPLICATION_JSON)
	Map<String, Object> jwkSet(String id) {
		return new JWKSet(jwks.getOrDefault(id, jwk("null"))).toJSONObject(true);
	}

	public JWTClaimsSet claims(String id, String email) {
		return claims(id, email, claims -> {});
	}

	public JWTClaimsSet claims(String id, String email, Consumer<JWTClaimsSet.Builder> claimsModifier) {
		var now = Instant.now();
		var claims = new JWTClaimsSet.Builder()
				.subject(UUID.randomUUID().toString())
				.issuer("http://localhost:" + port + "/endpoints/test/" + id)
				.claim("email", email)
				.issueTime(Date.from(now))
				.expirationTime(Date.from(now.plusSeconds(60)));
		claimsModifier.accept(claims);
		return claims.build();
	}

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

	public String jwt(User user) {
		return jwt(user.getEmail());
	}

	public String jwt(String email) {
		return jwt(email, claims -> {});
	}

	public String jwt(String email, Consumer<JWTClaimsSet.Builder> claimsModifier) {
		var id = jwks.keySet().iterator().next();
		return sign(id, claims(id, email, claimsModifier)).serialize();
	}

	public String jwt(String id, String email, Consumer<JWTClaimsSet.Builder> claimsModifier) {
		return sign(id, claims(id, email, claimsModifier)).serialize();
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
