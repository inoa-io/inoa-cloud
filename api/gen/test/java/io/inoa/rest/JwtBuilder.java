package io.inoa.rest;

import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import io.micronaut.security.token.config.TokenConfiguration;
import io.micronaut.security.token.jwt.signature.SignatureGeneratorConfiguration;

@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
public class JwtBuilder {

	private final JWTClaimsSet.Builder claims = new JWTClaimsSet.Builder();
	private final SignatureGeneratorConfiguration signature;

	public JwtBuilder(SignatureGeneratorConfiguration signature) {
		this.signature = signature;
	}

	public JwtBuilder subject(String subject) {
		claims.subject(subject);
		return this;
	}

	public JwtBuilder expirationTime(Instant timestamp) {
		claims.expirationTime(Date.from(timestamp));
		return this;
	}

	public JwtBuilder issuer(String issuer) {
		claims.issuer(issuer);
		return this;
	}

	public JwtBuilder issueTime(Instant timestamp) {
		claims.issueTime(Date.from(timestamp));
		return this;
	}

	public JwtBuilder audience(String audience) {
		claims.audience(audience);
		return this;
	}

	public JwtBuilder audience(String... audience) {
		claims.audience(List.of(audience));
		return this;
	}

	public JwtBuilder audience(Collection<String> audience) {
		claims.audience(List.copyOf(audience));
		return this;
	}

	public JwtBuilder roles(String role) {
		claims.claim(TokenConfiguration.DEFAULT_ROLES_NAME, role);
		return this;
	}

	public JwtBuilder roles(String... roles) {
		claims.claim(TokenConfiguration.DEFAULT_ROLES_NAME, List.of(roles));
		return this;
	}

	public JwtBuilder roles(Collection<String> roles) {
		claims.claim(TokenConfiguration.DEFAULT_ROLES_NAME, List.copyOf(roles));
		return this;
	}

	public JwtBuilder claim(String name, Object value) {
		claims.claim(name, value);
		return this;
	}

	public SignedJWT sign() {
		try {
			return signature.sign(claims.build());
		} catch (JOSEException e) {
			throw new IllegalStateException("Failed to sign JWT.", e);
		}
	}

	public String serialize() {
		return sign().serialize();
	}

	public String toBearer() {
		return "Bearer " + serialize();
	}
}
