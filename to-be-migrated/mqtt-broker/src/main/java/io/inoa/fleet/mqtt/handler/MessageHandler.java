package io.inoa.fleet.mqtt.handler;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;

import io.inoa.fleet.mqtt.Gateway;
import io.inoa.fleet.mqtt.KafkaHeader;
import io.micronaut.configuration.kafka.annotation.KafkaClient;
import io.moquette.interception.AbstractInterceptHandler;
import io.moquette.interception.messages.InterceptPublishMessage;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
		return new Class[] { InterceptPublishMessage.class };
	}

	@Override
	public void onPublish(InterceptPublishMessage message) {
		Gateway.of(message.getUsername()).mdc(gateway -> {

			log.trace("Gateway {}/{} sent message on topic {} with qos {}",
					gateway.tenantId(), gateway.gatewayId(), message.getTopicName(), message.getQos());

			var key = gateway.gatewayId();
			var topic = switch (message.getTopicName()) {
				case "t", "telemetry" -> "hono.telemetry." + gateway.tenantId();
				case "e", "event" -> "hono.event." + gateway.tenantId();
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
}
