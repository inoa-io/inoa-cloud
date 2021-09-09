package io.inoa.fleet.registry.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.text.ParseException;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.jwt.SignedJWT;

import io.inoa.fleet.registry.infrastructure.Auth;
import io.inoa.fleet.registry.infrastructure.ComposeTest;
import io.inoa.fleet.registry.rest.gateway.AuthApiClient;
import io.inoa.fleet.registry.rest.gateway.PropertiesApiClient;
import io.inoa.fleet.registry.rest.management.CredentialCreateVO;
import io.inoa.fleet.registry.rest.management.CredentialTypeVO;
import io.inoa.fleet.registry.rest.management.CredentialsApiClient;
import io.inoa.fleet.registry.rest.management.GatewayCreateVO;
import io.inoa.fleet.registry.rest.management.GatewaysApiClient;
import io.inoa.fleet.registry.rest.management.SecretCreatePSKVO;
import io.inoa.fleet.registry.rest.management.SecretCreatePasswordVO;
import io.inoa.fleet.registry.rest.management.TenantsApiClient;
import io.inoa.fleet.registry.test.BackupServicePrometheusClient;
import io.inoa.fleet.registry.test.GatewayMqttClient;
import io.micronaut.context.annotation.Value;
import io.micronaut.http.HttpHeaderValues;

/**
 * Test usecase: create tenant with gateway and token handling.
 *
 * @author Stephan Schnabel
 */
@DisplayName("usecase")
public class GatewayTest extends ComposeTest {

	@Inject
	GatewaysApiClient gatewaysClient;
	@Inject
	CredentialsApiClient credentialsClient;
	@Inject
	AuthApiClient authClient;
	@Inject
	PropertiesApiClient propertiesClient;
	@Inject
	TenantsApiClient tenantsApiClient;
	@Inject
	BackupServicePrometheusClient backupServicePrometheusClient;
	@Value("${mqtt.client.server-uri}")
	String mqttServerUrl;

	static UUID tenantId = UUID.fromString("2381b39a-e721-4456-8f9f-8d2c18cec993");
	static UUID gatewayId;
	static String secret;

	static String gatewayToken;
	static String userToken;

	@DisplayName("1. create gateway with psk secret")
	@Test
	void createGateway() {

		var vo = new GatewayCreateVO().setName("dev-" + UUID.randomUUID().toString().substring(0, 10));
		userToken = auth.user(Auth.USER_TENANT_A);
		gatewayId = assert201(() -> gatewaysClient.createGateway(userToken, vo)).getGatewayId();
		secret = UUID.randomUUID().toString();
		assert201(() -> credentialsClient.createCredential(userToken, gatewayId, new CredentialCreateVO()
				.setAuthId("registry")
				.setType(CredentialTypeVO.PSK)
				.setSecrets(List.of(new SecretCreatePSKVO().setSecret(secret.getBytes())))));
		assert201(() -> credentialsClient.createCredential(userToken, gatewayId, new CredentialCreateVO()
				.setAuthId("hono")
				.setType(CredentialTypeVO.PASSWORD)
				.setSecrets(List.of(new SecretCreatePasswordVO().setPassword(secret.getBytes())))));
	}

	@DisplayName("2. get registry token")
	@Test
	void getRegistryToken() {
		var response = assert200(() -> authClient.getToken(Auth.GRANT_TYPE, auth.hmac(gatewayId, secret)));
		gatewayToken = HttpHeaderValues.AUTHORIZATION_PREFIX_BEARER + " " + response.getAccessToken();
	}

	@DisplayName("3. validate registry token")
	@Test
	void validateRegistryToken() throws ParseException, JOSEException {
		var jwt = (SignedJWT) JWTParser.parse(gatewayToken.split(" ")[1]);
		var keyId = jwt.getHeader().getKeyID();
		var keys = auth.getKeys();
		var key = keys.getKeyByKeyId(keyId);
		assertNotNull(key, "key unknown");
		var verifier = new RSASSAVerifier(key.toRSAKey());
		assertTrue(verifier.verify(jwt.getHeader(), jwt.getSigningInput(), jwt.getSignature()), "signature validation");
	}

	@DisplayName("4. set and get properties")
	@Test
	void gatewayProperties() {
		var properties = Map.of("aa", "1", "bb", "2");
		assert200(() -> propertiesClient.setProperties(gatewayToken, properties));
		assertEquals(properties, assert200(() -> propertiesClient.getProperties(gatewayToken)));
		assertEquals(properties, assert200(() -> gatewaysClient.findGateway(userToken, gatewayId)).getProperties());
	}

	@DisplayName("5. send telemetry message to backup ")
	@Test
	void sendTelemetryToBackup() {
		var messagesBefore = backupServicePrometheusClient.scrapMessages();

		var payload = "uggaugga-" + UUID.randomUUID();
		var mqtt = new GatewayMqttClient(mqttServerUrl, tenantId, gatewayId, secret);
		mqtt.connect();
		mqtt.sendTelemetry(payload);

		Awaitility
				.await("wait for telemetry stored in backup")
				.pollInterval(Duration.ofMillis(500))
				.timeout(Duration.ofSeconds(30))
				.until(() -> backupServicePrometheusClient.scrapMessages() > messagesBefore);
	}
}
