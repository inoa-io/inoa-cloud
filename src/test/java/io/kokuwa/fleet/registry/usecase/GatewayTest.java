package io.kokuwa.fleet.registry.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import io.kokuwa.fleet.registry.AbstractTest;
import io.kokuwa.fleet.registry.rest.gateway.AuthApiTestClient;
import io.kokuwa.fleet.registry.rest.gateway.AuthController;
import io.kokuwa.fleet.registry.rest.gateway.PropertiesApiTestClient;
import io.kokuwa.fleet.registry.rest.management.GatewayApiTestClient;
import io.kokuwa.fleet.registry.rest.management.GatewayCreateVO;
import io.kokuwa.fleet.registry.rest.management.SecretApiTestClient;
import io.kokuwa.fleet.registry.rest.management.SecretCreateHmacVO;
import io.kokuwa.fleet.registry.rest.management.TenantApiTestClient;
import io.kokuwa.fleet.registry.rest.management.TenantCreateVO;
import io.micronaut.http.HttpHeaderValues;
import lombok.SneakyThrows;

/**
 * Test some usecases.
 *
 * @author Stephan Schnabel
 */
@DisplayName("usecase")
public class GatewayTest extends AbstractTest {

	@Inject
	TenantApiTestClient tenantClient;
	@Inject
	GatewayApiTestClient gatewayClient;
	@Inject
	SecretApiTestClient secretClient;
	@Inject
	AuthApiTestClient authClient;
	@Inject
	PropertiesApiTestClient propertiesClient;

	@DisplayName("create tenant/gateway, authenticate, set properties")
	@Test
	void gateway() {

		// create tenant with gateway

		var userToken = bearerAdmin();
		var tenantId = assert201(() -> tenantClient.createTenant(userToken, new TenantCreateVO()
				.setName("kokuwa")
				.setEnabled(true))).getTenantId();
		var gatewayId = assert201(() -> gatewayClient.createGateway(userToken, new GatewayCreateVO()
				.setTenantId(tenantId)
				.setName("device-1")
				.setEnabled(true))).getGatewayId();
		var hmac = UUID.randomUUID().toString();
		assert201(() -> secretClient.createSecret(userToken, gatewayId, new SecretCreateHmacVO()
				.setHmac(hmac.getBytes())
				.setName("hmac-1")
				.setEnabled(true)));

		// get token

		var response = assert200(() -> authClient.getToken(AuthController.GRANT_TYPE, gatewayToken(gatewayId, hmac)));
		var gatewayToken = HttpHeaderValues.AUTHORIZATION_PREFIX_BEARER + " " + response.getAccessToken();

		// set properties

		var gatewayProperties = Map.of("aa", "1", "bb", "2");
		assert200(() -> propertiesClient.setProperties(gatewayToken, gatewayProperties));

		// get properties

		var gateway = assert200(() -> gatewayClient.getGateway(userToken, gatewayId));
		assertEquals(gatewayProperties, gateway.getProperties());
	}

	@SneakyThrows
	private String gatewayToken(UUID gatewayId, String hmac) {
		var now = Instant.now();
		var claims = new JWTClaimsSet.Builder()
				.audience(properties.getGateway().getToken().getAudience())
				.jwtID(UUID.randomUUID().toString())
				.issuer(gatewayId.toString())
				.issueTime(Date.from(now))
				.notBeforeTime(Date.from(now))
				.expirationTime(Date.from(now.plusSeconds(1)));
		var jwt = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claims.build());
		jwt.sign(new MACSigner(hmac));
		return jwt.serialize();
	}
}
