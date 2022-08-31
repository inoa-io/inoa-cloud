package io.inoa.fleet.mqtt.handler;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;

import io.inoa.fleet.mqtt.Gateway;
import io.inoa.fleet.mqtt.MqttHeader;
import io.micronaut.configuration.kafka.annotation.KafkaClient;
import io.moquette.interception.AbstractInterceptHandler;
import io.moquette.interception.messages.InterceptPublishMessage;
import io.netty.handler.codec.mqtt.MqttQoS;
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

			if (message.getQos() != MqttQoS.AT_LEAST_ONCE) {
				log.warn("Gateway {}/{} sent message on topic {} with unsupported qos {}",
						gateway.tenantId(), gateway.gatewayId(), message.getTopicName(), message.getQos());
			} else {
				log.trace("Gateway {}/{} sent message on topic {} with qos {}",
						gateway.tenantId(), gateway.gatewayId(), message.getTopicName(), message.getQos());
			}

			var key = gateway.gatewayId().toString();
			var topic = toKafkaTopic(gateway, message);
			var payload = new byte[message.getPayload().readableBytes()];
			message.getPayload().readBytes(payload);
			var headers = List.<Header>of(
					new RecordHeader(MqttHeader.TENANT_ID, gateway.tenantId().getBytes()),
					new RecordHeader(MqttHeader.DEVICE_ID, gateway.gatewayId().toString().getBytes()),
					new RecordHeader(MqttHeader.CONTENT_TYPE, MqttHeader.CONTENT_TYPE_JSON.getBytes()),
					new RecordHeader(MqttHeader.CREATION_TIME, String.valueOf(System.currentTimeMillis()).getBytes()),
					new RecordHeader(MqttHeader.ORIG_ADDRESS, message.getTopicName().getBytes()),
					new RecordHeader(MqttHeader.QOS, String.valueOf(message.getQos().value()).getBytes()));
			var record = new ProducerRecord<>(topic, null, key, payload, headers);

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
