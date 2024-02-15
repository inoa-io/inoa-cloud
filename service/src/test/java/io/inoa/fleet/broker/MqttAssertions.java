package io.inoa.fleet.broker;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.StreamSupport;

import org.apache.kafka.clients.consumer.ConsumerRecord;

/**
 * Assertions for mqtt communication.
 *
 * @author stephan.schnabel@grayc.de
 */
public final class MqttAssertions {

	public static void assertHeader(ConsumerRecord<?, ?> record, String name, Object value) {
		var headers = StreamSupport.stream(record.headers().headers(name).spliterator(), false).toList();
		assertEquals(1, headers.size(), "header size for " + name);
		assertArrayEquals(value.toString().getBytes(), headers.get(0).value(), "header value for " + name);
	}
}
