package io.inoa.fleet.registry.test;

import static io.inoa.fleet.registry.rest.HttpResponseAssertions.assert200;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.jwt.SignedJWT;

import io.inoa.fleet.registry.rest.gateway.ConfigurationApiClient;
import io.inoa.fleet.registry.rest.gateway.PropertiesApiClient;
import io.inoa.hono.messages.HonoTelemetryMessageVO;
import io.micronaut.http.HttpHeaderValues;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@RequiredArgsConstructor
public class GatewayClient {

	private final AuthApiFixedClient authClient;
	private final PropertiesApiClient propertiesClient;
	private final ConfigurationApiClient configurationClient;

	@Getter
	private final String tenantId;
	@Getter
	private final UUID gatewayId;
	private final String secret;

	private String registryToken;
	private GatewayMqttClient mqttClient;

	// auth

	@SneakyThrows
	public GatewayClient fetchRegistryToken() {

		// create jwt from gateway secret as hmac

		var now = Instant.now();
		var claims = new JWTClaimsSet.Builder()
				.audience("gateway-registry")
				.jwtID(UUID.randomUUID().toString())
				.issuer(gatewayId.toString())
				.issueTime(Date.from(now))
				.notBeforeTime(Date.from(now))
				.expirationTime(Date.from(now.plusSeconds(10)));
		var jwt = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claims.build());
		jwt.sign(new MACSigner(secret));

		// call registry and store jwt

		registryToken = HttpHeaderValues.AUTHORIZATION_PREFIX_BEARER
				+ " "
				+ assert200(() -> authClient.getToken("urn:ietf:params:oauth:grant-type:jwt-bearer", jwt.serialize()))
						.body().accessToken();

		return this;
	}

	@SneakyThrows
	public SignedJWT getRegistryToken() {
		return (SignedJWT) JWTParser.parse(registryToken.split(" ")[1]);
	}

	// registry

	public Map<String, String> getProperties() {
		if (registryToken == null) {
			fetchRegistryToken();
		}
		return assert200(() -> propertiesClient.getProperties(registryToken)).body();
	}

	public Map<String, String> setProperties(Map<String, String> properties) {
		if (registryToken == null) {
			fetchRegistryToken();
		}
		return assert200(() -> propertiesClient.setProperties(registryToken, properties)).body();
	}

	public Map<String, Object> getConfiguration() {
		if (registryToken == null) {
			fetchRegistryToken();
		}
		return assert200(() -> configurationClient.getConfiguration(registryToken)).body();
	}

	// mqtt

	public GatewayMqttClient mqtt() {
		if (mqttClient == null) {
			var mqttUrl = getConfiguration().get("mqtt.url");
			assertNotNull(mqttUrl, "mqtt.url is null");
			mqttClient = new GatewayMqttClient(mqttUrl.toString(), tenantId, gatewayId, secret);
		}
		return mqttClient;
	}

	public static class GatewayMqttClient {

		private final ObjectMapper mapper = new ObjectMapper();
		private final String tenantId;
		private final UUID gatewayId;
		private final String password;
		private final MqttClient client;

		@SneakyThrows
		public GatewayMqttClient(
				String mqttServerUrl,
				String tenantId,
				UUID gatewayId,
				String password) {
			this.tenantId = tenantId;
			this.gatewayId = gatewayId;
			this.password = password;
			this.client = new MqttClient(mqttServerUrl, "test-client-" + UUID.randomUUID(), new MemoryPersistence());
		}

		@SneakyThrows
		public GatewayMqttClient connect() {
			var options = new MqttConnectOptions();
			options.setUserName(gatewayId + "@" + tenantId);
			options.setPassword(password.toCharArray());
			options.setCleanSession(true);
			this.client.connect(options);
			return this;
		}

		public GatewayMqttClient sendTelemetry(String urn, Instant timestamp, byte[] value) {
			sendTelemetry(
					new HonoTelemetryMessageVO().setUrn(urn).setTimestamp(timestamp.toEpochMilli()).setValue(value));
			return this;
		}

		@SneakyThrows
		public GatewayMqttClient sendTelemetry(HonoTelemetryMessageVO payload) {
			sendTelemetry(mapper.writeValueAsString(payload));
			return this;
		}

		@SneakyThrows
		public GatewayMqttClient sendTelemetry(String payload) {
			client.publish("telemetry", new MqttMessage(payload.getBytes()));
			return this;
		}

		@SneakyThrows
		public void close() {
			client.disconnectForcibly();
		}
	}
}
