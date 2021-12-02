package io.inoa.cnpm.tenant.test;

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
public class TestSecurity {

	private final Map<String, JWK> jwks = Map.of("0", jwk("0"), "1", jwk("1"), "2", jwk("2"));

	@Value("${micronaut.server.port}")
	int port;

	@Get("/endpoints/test/{id}/protocol/openid-connect/certs")
	@Produces(MediaType.APPLICATION_JSON)
	Map<String, Object> jwkSet(String id) {
		return new JWKSet(jwks.getOrDefault(id, jwk("null"))).toJSONObject(true);
	}

	@SneakyThrows
	public URL getOpenIDBase(String id) {
		return new URL("http://localhost:" + port + "/endpoints/test/" + id);
	}

	public JWTClaimsSet claims(String id, Consumer<JWTClaimsSet.Builder> claimsModifier) {
		var now = Instant.now();
		var claims = new JWTClaimsSet.Builder()
				.subject(UUID.randomUUID().toString())
				.issuer(getOpenIDBase(id).toString())
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

	public String jwtUser(User user) {
		return jwtUser(user.getEmail());
	}

	public String jwtUser(String email) {
		return jwtUser("0", email, claims -> {});
	}

	public String jwtUser(String email, Consumer<JWTClaimsSet.Builder> claimsModifier) {
		return jwtUser("0", email, claimsModifier);
	}

	public String jwtUser(String id, String email, Consumer<JWTClaimsSet.Builder> claimsModifier) {
		return sign(id, claims(id, claimsModifier.andThen(claims -> claims.claim("email", email)))).serialize();
	}

	public String jwtApplication(String id, Consumer<JWTClaimsSet.Builder> claimsModifier) {
		return sign(id, claims(id, claimsModifier)).serialize();
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
