package io.inoa.cnpm.tenant.auth;

import java.util.Optional;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import lombok.Value;

/**
 * Wrapper for {@link SignedJWT} and {@link JWTClaimsSet}.
 *
 * @author Stephan Schnabel
 */
@Value
public class Token {

	private final SignedJWT jwt;
	private final JWTClaimsSet claims;

	public Optional<String> getEmailClaim() {
		return Optional.ofNullable(claims.getClaim("email")).filter(String.class::isInstance).map(String.class::cast);
	}
}
