package io.inoa.fleet.registry.messaging;

import io.micronaut.configuration.kafka.annotation.KafkaClient;
import io.micronaut.configuration.kafka.annotation.Topic;
import io.micronaut.messaging.annotation.MessageHeader;

import java.util.UUID;

@KafkaClient
public interface TestProducer {
    @Topic("hono.event.test")
    void sendTestMessage(@MessageHeader("device_id") UUID gatewayId, @MessageHeader("tenant_id") String tenantId, @MessageHeader("content-type") String contentType, @MessageHeader("ttd") String ttd, String body);
}