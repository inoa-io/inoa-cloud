package io.inoa.cloud;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;

import io.inoa.cloud.messages.InoaTelemetryMessageVO;
import io.micronaut.configuration.kafka.annotation.KafkaListener;
import io.micronaut.configuration.kafka.annotation.OffsetReset;
import io.micronaut.configuration.kafka.annotation.Topic;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Kafka listener to export inoa messages to influx.
 *
 * @author Stephan Schnabel
 */
@KafkaListener(clientId = "exporter", groupId = "exporter", offsetReset = OffsetReset.EARLIEST)
@Slf4j
@RequiredArgsConstructor
public class InoaMessageListener {

	private final InfluxDBClient influx;

	@Topic(patterns = "inoa\\.telemetry\\..*")
	void receive(InoaTelemetryMessageVO message) {
		log.trace("Retrieved: {}", message);
		influx.getWriteApiBlocking().writePoint(Point
				.measurement("inoa")
				.addTag("tenant_id", message.getTenantId().toString())
				.addTag("gateway_id", message.getGatewayId().toString())
				.addTag("urn", message.getUrn())
				.addTag("device_id", message.getDeviceId())
				.addTag("type", message.getDeviceType())
				.addTag("sensor", message.getSensor())
				.time(message.getTimestamp(), WritePrecision.MS)
				.addField("value", message.getValue()));
	}
}
