package io.inoa.fleet.registry.auth;

import java.time.Clock;
import java.util.Date;
import java.util.UUID;
import java.util.function.Consumer;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import io.inoa.fleet.Data;
import io.inoa.fleet.FleetProperties;
import io.inoa.fleet.registry.domain.Credential;
import io.inoa.fleet.registry.domain.Gateway;
import io.micronaut.http.HttpHeaderValues;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

/**
 * Helper for issuing gateway tokens.
 *
 * @author Stephan Schnabel
 */
@Singleton
@RequiredArgsConstructor
public class GatewayTokenHelper {

	private final Clock clock;
	private final Data data;
	private final FleetProperties properties;

	public String bearer(Gateway gateway) {
		return bearer(data.findCredentials(gateway).stream().findFirst().orElse(data.credentialPSK(gateway)));
	}

	public String bearer(Credential credential) {
		return HttpHeaderValues.AUTHORIZATION_PREFIX_BEARER
				+ " "
				+ token(credential.getGateway().getGatewayId(), credential.getValue());
	}

	public String token(String gatewayId, byte[] secret) {
		return token(gatewayId, secret, claims -> {});
	}

	@SneakyThrows
	public String token(String gatewayId, byte[] secret, Consumer<JWTClaimsSet.Builder> claimsManipulator) {

		var claims = new JWTClaimsSet.Builder()
				.audience(properties.getGateway().getToken().getAudience())
				.jwtID(UUID.randomUUID().toString())
				.issuer(gatewayId)
				.issueTime(Date.from(clock.instant()))
				.notBeforeTime(Date.from(clock.instant()))
				.expirationTime(Date.from(clock.instant().plusSeconds(1)));
		claimsManipulator.accept(claims);

		var jwt = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claims.build());
		jwt.sign(new MACSigner(secret));
		return jwt.serialize();
	}
}
