package io.inoa.cloud;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;

import io.inoa.cloud.messages.InoaTelemetryMessageVO;
import io.micronaut.configuration.kafka.annotation.KafkaListener;
import io.micronaut.configuration.kafka.annotation.OffsetReset;
import io.micronaut.configuration.kafka.annotation.Topic;
import io.micronaut.core.util.CollectionUtils;
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
		var point = Point
				.measurement("inoa")
				.time(message.getTimestamp(), WritePrecision.MS)
				.addField("value", message.getValue());
		if (CollectionUtils.isNotEmpty(message.getExt())) {
			point.addTags(message.getExt());
		}
		influx.getWriteApiBlocking().writePoint(point
				.addTag("tenant_id", message.getTenantId())
				.addTag("gateway_id", message.getGatewayId().toString())
				.addTag("urn", message.getUrn())
				.addTag("device_id", message.getDeviceId())
				.addTag("type", message.getDeviceType())
				.addTag("sensor", message.getSensor()));
	}
}
