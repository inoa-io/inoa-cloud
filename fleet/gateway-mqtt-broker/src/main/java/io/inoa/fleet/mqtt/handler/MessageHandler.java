package io.inoa.fleet.mqtt.handler;

import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import io.inoa.fleet.mqtt.Gateway;
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
			log.trace("Got message on topic {} with qos {}", message.getTopicName(), message.getQos());

			var key = gateway.gatewayId().toString();
			var topic = toKafkaTopic(gateway, message);
			var bytes = new byte[message.getPayload().readableBytes()];
			message.getPayload().readBytes(bytes);
			var record = new ProducerRecord<>(topic, key, bytes);

			try {
				producer.send(record).get();
				log.trace("Send message to kafka topic {}", topic);
			} catch (InterruptedException | ExecutionException e) {
				log.warn("Failed to publish message", e);
				return;
			}
			message.getPayload().release();
		});
	}

	private String toKafkaTopic(Gateway gateway, InterceptPublishMessage message) {
		switch (message.getTopicName()) {
			case "t":
			case "telemetry":
				return "hono.telemetry." + gateway.tenantId();
			case "e":
			case "event":
				return "hono.event." + gateway.tenantId();
		}
		return null;
	}
}
