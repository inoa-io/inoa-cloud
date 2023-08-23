package io.inoa.fleet.broker;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.inoa.fleet.model.TelemetryRawVO;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;

/**
 * Client to be used by external services. Use
 * {@link MqttBroker#internalPublish(MqttPublishMessage, String)} to publish
 * internal messages.
 */
@AllArgsConstructor
public class MqttServiceClient {

	private final ObjectMapper mapper = new ObjectMapper();
	@Getter
	private final String clientId = MqttServiceClient.class.getName();
	private final MqttClient client;
	private final MqttConnectOptions options;

	public MqttServiceClient(String url, String tenantId, String gatewayId, byte[] psk) throws MqttException {
		this.client = new MqttClient(url, clientId, new MemoryPersistence());
		this.options = new MqttConnectOptions();
		this.options.setUserName(gatewayId + "@" + tenantId);
		this.options.setPassword(new String(psk).toCharArray());
		this.options.setCleanSession(true);
	}

	public MqttClient client() {
		return this.client;
	}

	@SneakyThrows(GeneralSecurityException.class)
	public MqttServiceClient trustAllCertificates() {
		var trustManager = new X509TrustManager() {

			@Override
			public void checkClientTrusted(X509Certificate[] chain, String authType) {
			}

			@Override
			public void checkServerTrusted(X509Certificate[] chain, String authType) {
			}

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

	public MqttServiceClient connect() throws MqttException {
		this.client.connect(options);
		return this;
	}

	public void disconnect() throws MqttException {
		client.disconnect();
	}

	public void disconnectWithoutNotification() throws MqttException {
		client.disconnectForcibly(0, 0, false);
	}

	public void subscribe(String topicFilter, IMqttMessageListener messageListener) throws MqttException {
		client.subscribe(topicFilter, messageListener);
	}

	public MqttServiceClient publish(String topic, byte[] payload) throws MqttException {
		client.publish(topic, new MqttMessage(payload));
		return this;
	}

	@SneakyThrows(IOException.class)
	public MqttServiceClient publishTelemetry(TelemetryRawVO vo) throws MqttException {
		return publish("telemetry", mapper.writeValueAsBytes(vo));
	}

	public MqttServiceClient publishTelemetry(byte[] payload) throws MqttException {
		return publish("telemetry", payload);
	}

	public MqttServiceClient publishEvent(byte[] payload) throws MqttException {
		return publish("event", payload);
	}
}
