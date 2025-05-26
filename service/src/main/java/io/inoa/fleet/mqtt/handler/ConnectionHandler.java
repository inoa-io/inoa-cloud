package io.inoa.fleet.mqtt.handler;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.inoa.fleet.mqtt.MqttGatewayIdentifier;
import io.inoa.messaging.KafkaHeader;
import io.micronaut.configuration.kafka.annotation.KafkaClient;
import io.moquette.interception.AbstractInterceptHandler;
import io.moquette.interception.messages.InterceptConnectMessage;
import io.moquette.interception.messages.InterceptConnectionLostMessage;
import io.moquette.interception.messages.InterceptDisconnectMessage;
import lombok.extern.slf4j.Slf4j;

/**
 * Handler for connect/disconnect events.
 *
 * @author Stephan Schnabel
 * @see "https://www.eclipse.org/hono/docs/api/event/#connection-event"
 */
@Singleton
@Slf4j
public class ConnectionHandler extends AbstractInterceptHandler {

	@Inject
	@KafkaClient
	Producer<String, Map<String, String>> producer;
	@Inject
	ObjectMapper mapper;

	@Override
	public String getID() {
		return "connection";
	}

	@Override
	public Class<?>[] getInterceptedMessageTypes() {
		return new Class<?>[] {
				InterceptConnectMessage.class,
				InterceptDisconnectMessage.class,
				InterceptConnectionLostMessage.class
		};
	}

	@Override
	public void onConnect(InterceptConnectMessage message) {
		MqttGatewayIdentifier.of(message.getUsername())
				.mdc(
						gateway -> {
							log.debug("Gateway {}/{} connected", gateway.tenantId(), gateway.gatewayId());
							event(message.getClientID(), gateway, true);
						});
	}

	@Override
	public void onDisconnect(InterceptDisconnectMessage message) {
		MqttGatewayIdentifier.of(message.getUsername())
				.mdc(
						gateway -> {
							log.debug("Gateway {}/{} disconnected", gateway.tenantId(), gateway.gatewayId());
							// no event is emitted because this is triggered by a disconnect package from the
							// client
							// connection lost is alwyas triggered after this disconnect message
							// disconnect event is created by `onConnectionLost`
						});
	}

	@Override
	public void onConnectionLost(InterceptConnectionLostMessage message) {
		MqttGatewayIdentifier.of(message.getUsername())
				.mdc(
						gateway -> {
							log.debug("Gateway {}/{} lost connection", gateway.tenantId(), gateway.gatewayId());
							event(message.getClientID(), gateway, false);
						});
	}

	private void event(String clientId, MqttGatewayIdentifier gateway, boolean connected) {

		var key = gateway.gatewayId();
		var topic = "hono.event." + gateway.tenantId();
		var payload = Map.of(
				"cause",
				connected ? "connected" : "disconnected",
				"remote-id",
				clientId,
				"source",
				"inoa-mqtt");
		var headers = List.<Header>of(
				new RecordHeader(KafkaHeader.TENANT_ID, gateway.tenantId().getBytes()),
				new RecordHeader(KafkaHeader.DEVICE_ID, gateway.gatewayId().getBytes()),
				new RecordHeader(
						KafkaHeader.CONTENT_TYPE, KafkaHeader.CONTENT_TYPE_EVENT_DC.getBytes()),
				new RecordHeader(
						KafkaHeader.CREATION_TIME, String.valueOf(System.currentTimeMillis()).getBytes()),
				new RecordHeader(KafkaHeader.QOS, "1".getBytes()));

		try {
			producer.send(new ProducerRecord<>(topic, null, key, payload, headers)).get();
		} catch (InterruptedException | ExecutionException e) {
			log.warn("Failed to publish message", e);
			return;
		}
	}

	@Override
	public void onSessionLoopError(Throwable error) {
		log.warn("Got session loop error", error);
	}
}
