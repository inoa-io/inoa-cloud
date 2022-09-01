package io.inoa.fleet.mqtt.handler;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.inoa.fleet.mqtt.Gateway;
import io.inoa.fleet.mqtt.MqttHeader;
import io.micronaut.configuration.kafka.annotation.KafkaClient;
import io.moquette.interception.AbstractInterceptHandler;
import io.moquette.interception.messages.InterceptConnectMessage;
import io.moquette.interception.messages.InterceptConnectionLostMessage;
import io.moquette.interception.messages.InterceptDisconnectMessage;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
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
		return new Class[] {
				InterceptConnectMessage.class,
				InterceptDisconnectMessage.class,
				InterceptConnectionLostMessage.class };
	}

	@Override
	public void onConnect(InterceptConnectMessage message) {
		Gateway.of(message.getUsername()).mdc(gateway -> {
			log.debug("Gateway {}/{} connected", gateway.tenantId(), gateway.gatewayId());
			event(message.getClientID(), gateway, true);
		});
	}

	@Override
	public void onDisconnect(InterceptDisconnectMessage message) {
		Gateway.of(message.getUsername()).mdc(gateway -> {
			log.debug("Gateway {}/{} disconnected", gateway.tenantId(), gateway.gatewayId());
			// no event is emitted because this is triggered by a disconnect package from the client
			// connection lost is alwyas triggered after this disconnect message
			// disconnect event is created by `onConnectionLost`
		});
	}

	@Override
	public void onConnectionLost(InterceptConnectionLostMessage message) {
		Gateway.of(message.getUsername()).mdc(gateway -> {
			log.debug("Gateway {}/{} lost connection", gateway.tenantId(), gateway.gatewayId());
			event(message.getClientID(), gateway, false);
		});
	}

	private void event(String clientId, Gateway gateway, boolean connected) {

		var key = gateway.gatewayId().toString();
		var topic = "hono.event." + gateway.tenantId();
		var payload = Map.of(
				"cause", connected ? "connected" : "disconnected",
				"remote-id", clientId,
				"source", "inoa-mqtt");
		var headers = List.<Header>of(
				new RecordHeader(MqttHeader.TENANT_ID, gateway.tenantId().getBytes()),
				new RecordHeader(MqttHeader.DEVICE_ID, gateway.gatewayId().toString().getBytes()),
				new RecordHeader(MqttHeader.CONTENT_TYPE, MqttHeader.CONTENT_TYPE_EVENT_DC.getBytes()),
				new RecordHeader(MqttHeader.CREATION_TIME, String.valueOf(System.currentTimeMillis()).getBytes()),
				new RecordHeader(MqttHeader.QOS, "1".getBytes()));

		try {
			producer.send(new ProducerRecord<>(topic, null, key, payload, headers)).get();
		} catch (InterruptedException | ExecutionException e) {
			log.warn("Failed to publish message", e);
			return;
		}
	}
}
