package io.inoa.fleet.registry.usecase;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.text.ParseException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.jwt.SignedJWT;

import io.inoa.fleet.registry.infrastructure.Auth;
import io.inoa.fleet.registry.infrastructure.ComposeTest;
import io.inoa.fleet.registry.rest.management.CredentialCreateVO;
import io.inoa.fleet.registry.rest.management.CredentialTypeVO;
import io.inoa.fleet.registry.rest.management.GatewayCreateVO;
import io.inoa.fleet.registry.rest.management.SecretCreatePSKVO;
import io.inoa.fleet.registry.rest.management.SecretCreatePasswordVO;
import io.inoa.fleet.registry.test.GatewayMqttClient;
import io.inoa.hono.messages.HonoTelemetryMessageVO;
import io.micronaut.http.HttpHeaderValues;

/**
 * Test usecase: create tenant with gateway and token handling.
 *
 * @author Stephan Schnabel
 */
@DisplayName("usecase")
public class GatewayTest extends ComposeTest {

	static UUID tenantId = UUID.fromString("2381b39a-e721-4456-8f9f-8d2c18cec993");
	static UUID gatewayId;
	static String secret;

	static String gatewayToken;
	static String userToken;

	static long backupMessagesBefore;
	static long inoaTranslatorSuccessBefore;
	static long inoaExporterKafkaRecordsBefore;
	static HonoTelemetryMessageVO payload;

	@DisplayName("0. collect preconditions")
	@Test
	void preconditions() {
		backupMessagesBefore = kafkaBackupPrometheusClient.scrapMessages();
		inoaTranslatorSuccessBefore = inoaTranslatorPrometheusClient.scrapMessagesSuccess();
		inoaExporterKafkaRecordsBefore = inoaExporterPrometheusClient.scrapKafkaRecords();
	}

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

	@DisplayName("5. send telemetry")
	@Test
	void sendTelemetry() {
		payload = new HonoTelemetryMessageVO()
				.setTimestamp(Instant.now().toEpochMilli())
				.setUrn("urn:dvh4013:0815:meh")
				.setValue("123.456".getBytes());
		var mqtt = new GatewayMqttClient(mqttServerUrl, tenantId, gatewayId, secret);
		mqtt.connect();
		mqtt.sendTelemetry(payload);
	}

	@DisplayName("6. wait for message stored in backup")
	@Test
	void waitForBackup() {
		Awaitility
				.await("wait for message stored in backup")
				.pollInterval(Duration.ofMillis(500))
				.timeout(Duration.ofSeconds(30))
				.until(() -> kafkaBackupPrometheusClient.scrapMessages() > backupMessagesBefore);
	}

	@DisplayName("7. wait for message translated to inoa message")
	@Test
	void waitForTranslator() {
		Awaitility
				.await("wait for message translated to inoa message")
				.pollInterval(Duration.ofMillis(500))
				.timeout(Duration.ofSeconds(30))
				.until(() -> inoaTranslatorPrometheusClient.scrapMessagesSuccess() > inoaTranslatorSuccessBefore);
	}

	@DisplayName("8. wait for message in influx")
	@Test
	void waitForInfluxdb() {
		Awaitility
				.await("wait for message translated to inoa message")
				.pollInterval(Duration.ofMillis(500))
				.timeout(Duration.ofSeconds(30))
				.until(() -> inoaExporterPrometheusClient.scrapKafkaRecords() > inoaExporterKafkaRecordsBefore);
	}

	@DisplayName("9. check message in influx")
	@Test
	void checkInfluxdb() {
		var query = "from(bucket:\"export\")"
				+ " |> range(start: -10h)"
				+ " |> filter(fn: (r) => r.gateway_id == \"" + gatewayId + "\")";
		var tables = influxdb.getQueryApi().query(query);
		assertEquals(1, tables.size(), "tables");
		assertEquals(1, tables.get(0).getRecords().size(), "records");
		var record = tables.get(0).getRecords().get(0);
		assertAll("record",
				() -> assertEquals("inoa", record.getMeasurement(), "measurement"),
				() -> assertEquals(tenantId.toString(), record.getValueByKey("tenant_id"), "tenant_id"),
				() -> assertEquals(gatewayId.toString(), record.getValueByKey("gateway_id"), "gateway_id"),
				() -> assertEquals("urn:dvh4013:0815:meh", record.getValueByKey("urn"), "urn"),
				() -> assertEquals("dvh4013", record.getValueByKey("type"), "type"),
				() -> assertEquals("0815", record.getValueByKey("device_id"), "device_id"),
				() -> assertEquals("meh", record.getValueByKey("sensor"), "sensor"),
				() -> assertEquals(Instant.ofEpochMilli(payload.getTimestamp()), record.getTime(), "timestamp"),
				() -> assertEquals(123.456D, record.getValue(), "value"));
	}
}
