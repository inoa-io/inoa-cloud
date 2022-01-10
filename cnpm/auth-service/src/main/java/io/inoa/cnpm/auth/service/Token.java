package io.inoa.cnpm.auth.service;

import java.text.ParseException;
import java.util.List;
import java.util.Optional;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import io.micronaut.security.token.config.TokenConfiguration;
import lombok.Value;

/**
 * Wrapper for {@link SignedJWT} and {@link JWTClaimsSet}.
 */
@Value
public class Token {

	public static final String CLAIM_AZP = "azp";
	public static final String CLAIM_TENANT_ID = "tenantId";
	public static final String CLAIM_USER_ID = "userId";
	public static final String CLAIM_EMAIL = "email";
	public static final String CLAIM_ROLES = TokenConfiguration.DEFAULT_ROLES_NAME;

	private final SignedJWT jwt;
	private final JWTClaimsSet claims;
	private final Optional<String> issuer;
	private final List<String> audience;
	private final Optional<String> azp;
	private final Optional<String> email;

	Token(String value) throws ParseException {
		this.jwt = SignedJWT.parse(value);
		this.claims = jwt.getJWTClaimsSet();
		this.issuer = Optional.ofNullable(claims.getIssuer());
		this.audience = claims.getAudience();
		this.azp = get(CLAIM_AZP);
		this.email = get(CLAIM_EMAIL);
	}

	private Optional<String> get(String claim) {
		return Optional.ofNullable(claims.getClaim(claim)).filter(String.class::isInstance).map(String.class::cast);
	}
}
