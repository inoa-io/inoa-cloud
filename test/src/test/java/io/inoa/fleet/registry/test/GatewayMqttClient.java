package io.inoa.fleet.registry.test;

import java.util.UUID;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.inoa.hono.messages.HonoTelemetryMessageVO;
import lombok.SneakyThrows;

public class GatewayMqttClient {

	private final ObjectMapper mapper = new ObjectMapper();
	private final UUID tenantId;
	private final UUID gatewayId;
	private final String password;
	private final MqttClient client;

	@SneakyThrows
	public GatewayMqttClient(
			String mqttServerUrl,
			UUID tenantId,
			UUID gatewayId,
			String password) {
		this.tenantId = tenantId;
		this.gatewayId = gatewayId;
		this.password = password;
		this.client = new MqttClient(mqttServerUrl, "test-client-" + UUID.randomUUID(), new MemoryPersistence());
	}

	@SneakyThrows
	public void connect() {
		var options = new MqttConnectOptions();
		options.setUserName(gatewayId + "@" + tenantId);
		options.setPassword(password.toCharArray());
		options.setCleanSession(true);
		this.client.connect(options);
	}

	@SneakyThrows
	public void sendTelemetry(HonoTelemetryMessageVO payload) {
		sendTelemetry(mapper.writeValueAsString(payload));
	}

	@SneakyThrows
	public void sendTelemetry(String payload) {
		client.publish("telemetry", new MqttMessage(payload.getBytes()));
	}

	@SneakyThrows
	public void close() {
		client.disconnectForcibly();
	}
}
