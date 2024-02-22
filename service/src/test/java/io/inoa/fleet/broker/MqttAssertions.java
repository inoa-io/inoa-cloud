package io.inoa.fleet.broker;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.StreamSupport;

import org.apache.kafka.clients.producer.ProducerRecord;

import io.inoa.KafkaSink;
import io.inoa.fleet.registry.KafkaHeader;

/**
 * Assertions for mqtt communication.
 *
 * @author stephan.schnabel@grayc.de
 */
public final class MqttAssertions {

	public static void assertHeader(ProducerRecord<?, ?> record, String name, Object value) {
		var headers = StreamSupport.stream(record.headers().headers(name).spliterator(), false).toList();
		assertEquals(1, headers.size(), "header size for " + name);
		assertArrayEquals(value.toString().getBytes(), headers.get(0).value(), "header value for " + name);
	}

	public static void assertConnectionEvent(KafkaSink kafka, String tenantId, String gatewayId, boolean connected) {
		var record = kafka.awaitHonoEvent(tenantId, gatewayId);
		assertEquals(connected ? "connected" : "disconnected", record.value().get("cause"), "cause invalid");
		assertHeader(record, KafkaHeader.CONTENT_TYPE, KafkaHeader.CONTENT_TYPE_EVENT_DC);
	}
}
