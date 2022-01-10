package io.inoa.measurement.exporter.messaging;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;

import io.inoa.measurement.telemetry.TelemetryVO;
import io.micronaut.configuration.kafka.annotation.KafkaListener;
import io.micronaut.configuration.kafka.annotation.OffsetReset;
import io.micronaut.configuration.kafka.annotation.Topic;
import io.micronaut.core.util.CollectionUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@KafkaListener(offsetReset = OffsetReset.EARLIEST, redelivery = true)
@Slf4j
@RequiredArgsConstructor
public class TelemetryListener {

	private final InfluxDBClient influx;

	@Topic(patterns = "inoa\\.telemetry\\..*")
	void receive(TelemetryVO telemetry) {
		log.trace("Retrieved: {}", telemetry);
		var point = Point
				.measurement("inoa")
				.time(telemetry.getTimestamp(), WritePrecision.MS)
				.addField("value", telemetry.getValue());
		if (CollectionUtils.isNotEmpty(telemetry.getExt())) {
			point.addTags(telemetry.getExt());
		}
		influx.getWriteApiBlocking().writePoint(point
				.addTag("tenant_id", telemetry.getTenantId())
				.addTag("gateway_id", telemetry.getGatewayId().toString())
				.addTag("urn", telemetry.getUrn())
				.addTag("device_id", telemetry.getDeviceId())
				.addTag("type", telemetry.getDeviceType())
				.addTag("sensor", telemetry.getSensor()));
	}
}
