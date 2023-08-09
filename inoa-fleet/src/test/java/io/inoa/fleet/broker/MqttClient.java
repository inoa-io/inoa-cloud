package io.inoa.fleet.broker;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;
import java.util.UUID;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.inoa.fleet.model.TelemetryRawVO;
import lombok.Getter;
import lombok.SneakyThrows;

public class MqttClient {

	private final ObjectMapper mapper = new ObjectMapper();
	@Getter
	private final String clientId = "test" + UUID.randomUUID().toString();
	private final org.eclipse.paho.client.mqttv3.MqttClient client;
	private final MqttConnectOptions options;

	public MqttClient(String url, String tenantId, String gatewayId, byte[] psk) throws MqttException {
		this.client = new org.eclipse.paho.client.mqttv3.MqttClient(url, clientId, new MemoryPersistence());
		this.options = new MqttConnectOptions();
		this.options.setUserName(gatewayId + "@" + tenantId);
		this.options.setPassword(new String(psk).toCharArray());
		this.options.setCleanSession(true);
	}

	public org.eclipse.paho.client.mqttv3.MqttClient client() {
		return this.client;
	}

	@SneakyThrows(GeneralSecurityException.class)
	public MqttClient trustAllCertificates() {
		var trustManager = new X509TrustManager() {

			@Override
			public void checkClientTrusted(X509Certificate[] chain, String authType) {}

			@Override
			public void checkServerTrusted(X509Certificate[] chain, String authType) {}

			@Override
			public X509Certificate[] getAcceptedIssuers() {
				return new X509Certificate[0];
			}
		};
		var sslContext = SSLContext.getInstance("TLS");
		sslContext.init(null, new TrustManager[] { trustManager }, null);
		this.options.setSocketFactory(sslContext.getSocketFactory());
		this.options.setHttpsHostnameVerificationEnabled(false);
		return this;
	}

	public MqttClient connect() throws MqttException {
		this.client.connect(options);
		return this;
	}

	public void disconnect() throws MqttException {
		client.disconnect();
	}

	public void disconnectWithoutNotification() throws MqttException {
		client.disconnectForcibly(0, 0, false);
	}

	public MqttClient publish(String topic, byte[] payload) throws MqttException {
		client.publish(topic, new MqttMessage(payload));
		return this;
	}

	@SneakyThrows(IOException.class)
	public MqttClient publishTelemetry(TelemetryRawVO vo) throws MqttException {
		return publish("telemetry", mapper.writeValueAsBytes(vo));
	}

	public MqttClient publishTelemetry(byte[] payload) throws MqttException {
		return publish("telemetry", payload);
	}

	public MqttClient publishEvent(byte[] payload) throws MqttException {
		return publish("event", payload);
	}
}
