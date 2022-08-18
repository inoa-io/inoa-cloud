package io.inoa.mqtt;

import java.util.UUID;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class HonoMqttClient {

	private final MqttClient client;
	private final MqttConnectOptions options;

	public HonoMqttClient(String url, String tenantId, UUID gatewayId, byte[] psk) throws MqttException {
		this.client = new MqttClient(url, "something", new MemoryPersistence());
		this.options = new MqttConnectOptions();
		this.options.setUserName(gatewayId + "@" + tenantId);
		this.options.setPassword(new String(psk).toCharArray());
		this.options.setCleanSession(true);
	}

	public MqttClient client() {
		return this.client;
	}

	public HonoMqttClient connect() throws MqttException {
		this.client.connect(options);
		return this;
	}

	public void disconnect() throws MqttException {
		client.disconnect();

	}

	public HonoMqttClient publish(String topic, byte[] payload) throws MqttException {
		client.publish(topic, new MqttMessage(payload));
		return this;
	}

	public HonoMqttClient publishTelemetry(byte[] payload) throws MqttException {
		return publish("telemetry", payload);
	}

	public HonoMqttClient publishEvent(byte[] payload) throws MqttException {
		return publish("event", payload);
	}
}
