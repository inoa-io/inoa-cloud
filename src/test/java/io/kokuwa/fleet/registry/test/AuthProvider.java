package io.kokuwa.fleet.registry.test;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import java.util.function.Consumer;

import javax.inject.Singleton;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import io.kokuwa.fleet.registry.ApplicationProperties;
import io.kokuwa.fleet.registry.auth.AuthTokenService;
import io.micronaut.http.HttpHeaderValues;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

/**
 * Service to provide valid jwt.
 *
 * @author Stephan Schnabel
 */
@Singleton
@RequiredArgsConstructor
public class AuthProvider {

	private final Instant now;
	private final ApplicationProperties properties;
	private final AuthTokenService authTokenService;

	public String gatewayToken(UUID gateway, String secret) {
		return gatewayToken(gateway, secret, null);
	}

	@SneakyThrows
	public String gatewayToken(UUID gateway, String secret, Consumer<JWTClaimsSet.Builder> claimsManipulator) {

		var claims = new JWTClaimsSet.Builder()
				.audience(properties.getGateway().getToken().getAudience())
				.jwtID(UUID.randomUUID().toString())
				.issuer(gateway.toString())
				.issueTime(Date.from(now))
				.notBeforeTime(Date.from(now))
				.expirationTime(Date.from(now.plusSeconds(1)));
		if (claimsManipulator != null) {
			claimsManipulator.accept(claims);
		}

		var jwt = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claims.build());
		jwt.sign(new MACSigner(secret));
		return jwt.serialize();
	}

	public String bearer(UUID gateway) {
		return HttpHeaderValues.AUTHORIZATION_PREFIX_BEARER + " " + authTokenService.createToken(gateway);
	}
}
