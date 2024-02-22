package io.inoa.fleet.broker.handler;

import static io.inoa.fleet.broker.MqttBroker.COMMAND_RESPONSE_TOPIC_LONG_MATCHER;
import static io.inoa.fleet.broker.MqttBroker.COMMAND_RESPONSE_TOPIC_SHORT_MATCHER;
import static io.inoa.fleet.broker.MqttBroker.EVENT_TOPIC_LONG_NAME;
import static io.inoa.fleet.broker.MqttBroker.EVENT_TOPIC_SHORT_NAME;
import static io.inoa.fleet.broker.MqttBroker.TELEMETRY_TOPIC_LONG_NAME;
import static io.inoa.fleet.broker.MqttBroker.TELEMETRY_TOPIC_SHORT_NAME;
import static io.inoa.fleet.broker.MqttBroker.matchesTopic;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;

import io.inoa.fleet.broker.MqttGatewayIdentifier;
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
	public void onPublish(InterceptPublishMessage message) {
		MqttGatewayIdentifier.of(message.getUsername()).mdc(gateway -> {

			log.trace("Gateway {}/{} sent message on topic {} with qos {}", gateway.tenantId(), gateway.gatewayId(),
					message.getTopicName(), message.getQos());

			var key = gateway.gatewayId();

			// We will ignore all RPC responses
			if (matchesTopic(message.getTopicName(), COMMAND_RESPONSE_TOPIC_LONG_MATCHER)
					|| matchesTopic(message.getTopicName(), COMMAND_RESPONSE_TOPIC_SHORT_MATCHER)) {
				return;
			}

			// Check for valid topics
			var topic = switch (message.getTopicName()) {
				case TELEMETRY_TOPIC_SHORT_NAME, TELEMETRY_TOPIC_LONG_NAME -> "hono.telemetry." + gateway.tenantId();
				case EVENT_TOPIC_SHORT_NAME, EVENT_TOPIC_LONG_NAME -> "hono.event." + gateway.tenantId();
				// should not happen because `InoaAuthorizator#canWrite` will not allow this
				default -> throw new IllegalArgumentException("Unexpected topic: " + message.getTopicName());
			};
			var payload = new byte[message.getPayload().readableBytes()];
			message.getPayload().readBytes(payload);
			var headers = List.<Header>of(
					new RecordHeader(KafkaHeader.TENANT_ID, gateway.tenantId().getBytes()),
					new RecordHeader(KafkaHeader.DEVICE_ID, gateway.gatewayId().getBytes()),
					new RecordHeader(KafkaHeader.CONTENT_TYPE, KafkaHeader.CONTENT_TYPE_JSON.getBytes()),
					new RecordHeader(KafkaHeader.CREATION_TIME, String.valueOf(System.currentTimeMillis()).getBytes()),
					new RecordHeader(KafkaHeader.ORIG_ADDRESS, message.getTopicName().getBytes()),
					new RecordHeader(KafkaHeader.QOS, String.valueOf(message.getQos().value()).getBytes()));

			try {
				producer.send(new ProducerRecord<>(topic, null, key, payload, headers)).get();
				log.trace("Send message to kafka topic {}", topic);
			} catch (InterruptedException | ExecutionException e) {
				log.warn("Failed to publish message", e);
				return;
			}
			message.getPayload().release();
		});
	}

	@Override
	public void onSessionLoopError(Throwable error) {
		log.warn("Got session loop error", error);
	}
}
