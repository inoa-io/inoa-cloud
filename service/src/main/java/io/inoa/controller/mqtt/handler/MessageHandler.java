package io.inoa.controller.mqtt.handler;

import static io.inoa.controller.mqtt.MqttBroker.COMMAND_RESPONSE_TOPIC_LONG_MATCHER;
import static io.inoa.controller.mqtt.MqttBroker.COMMAND_RESPONSE_TOPIC_SHORT_MATCHER;
import static io.inoa.controller.mqtt.MqttBroker.EVENT_TOPIC_LONG_NAME;
import static io.inoa.controller.mqtt.MqttBroker.EVENT_TOPIC_SHORT_NAME;
import static io.inoa.controller.mqtt.MqttBroker.TELEMETRY_TOPIC_LONG_NAME;
import static io.inoa.controller.mqtt.MqttBroker.TELEMETRY_TOPIC_SHORT_NAME;
import static io.inoa.controller.mqtt.MqttBroker.matchesTopic;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;

import io.inoa.controller.mqtt.MqttGatewayIdentifier;
import io.inoa.fleet.registry.KafkaHeader;
import io.micronaut.configuration.kafka.annotation.KafkaClient;
import io.moquette.interception.AbstractInterceptHandler;
import io.moquette.interception.messages.InterceptPublishMessage;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Handles MQTT messages from the broker and forwards them to Kafka.
 */
@Singleton
@Slf4j
@RequiredArgsConstructor
public class MessageHandler extends AbstractInterceptHandler {

	@Inject
	@KafkaClient
	Producer<String, byte[]> producer;

	@Override
	public String getID() {
		return "message";
	}

	@Override
	public Class<?>[] getInterceptedMessageTypes() {
		return new Class<?>[] { InterceptPublishMessage.class };
	}

	@Override
	public void onSessionLoopError(Throwable error) {
		log.warn("Got session loop error", error);
	}

	@Override
	public void onPublish(InterceptPublishMessage message) {
		MqttGatewayIdentifier.of(message.getUsername()).mdc(gateway -> {
			try {
				handle(gateway, message);
			} finally {
				message.getPayload().release();
			}
		});
	}

	private void handle(MqttGatewayIdentifier gateway, InterceptPublishMessage message) {

		var tenantId = gateway.tenantId();
		var gatewayId = gateway.gatewayId();
		var topicIn = message.getTopicName();
		var qos = message.getQos();

		log.trace("Gateway {}/{} sent message on topic {} with qos {}", tenantId, gatewayId, topicIn, qos);

		// We will ignore all RPC responses
		if (matchesTopic(topicIn, COMMAND_RESPONSE_TOPIC_LONG_MATCHER)
				|| matchesTopic(topicIn, COMMAND_RESPONSE_TOPIC_SHORT_MATCHER)) {
			return;
		}

		// Check for valid topics
		var topicOut = switch (topicIn) {
			case TELEMETRY_TOPIC_SHORT_NAME, TELEMETRY_TOPIC_LONG_NAME -> "hono.telemetry." + tenantId;
			case EVENT_TOPIC_SHORT_NAME, EVENT_TOPIC_LONG_NAME -> "hono.event." + tenantId;
			// should not happen because `InoaAuthorizator#canWrite` will not allow this
			default -> null;
		};
		if (topicOut == null) {
			log.warn("Gateway {}/{} sent message to unsupported topic {} with payload: {}");
			return;
		}

		var payload = new byte[message.getPayload().readableBytes()];
		message.getPayload().readBytes(payload);
		var headers = List.<Header>of(
				new RecordHeader(KafkaHeader.TENANT_ID, tenantId.getBytes()),
				new RecordHeader(KafkaHeader.DEVICE_ID, gatewayId.getBytes()),
				new RecordHeader(KafkaHeader.CONTENT_TYPE, KafkaHeader.CONTENT_TYPE_JSON.getBytes()),
				new RecordHeader(KafkaHeader.CREATION_TIME, String.valueOf(System.currentTimeMillis()).getBytes()),
				new RecordHeader(KafkaHeader.ORIG_ADDRESS, topicIn.getBytes()),
				new RecordHeader(KafkaHeader.QOS, String.valueOf(qos.value()).getBytes()));

		try {
			producer.send(new ProducerRecord<>(topicOut, null, gatewayId, payload, headers)).get();
			log.trace("Send message to kafka topic {}", topicOut);
		} catch (InterruptedException | ExecutionException e) {
			log.warn("Failed to publish message", e);
			return;
		}
	}
}
