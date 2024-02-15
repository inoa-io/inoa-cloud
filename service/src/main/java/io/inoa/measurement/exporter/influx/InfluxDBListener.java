package io.inoa.measurement.exporter.influx;

import org.slf4j.MDC;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;

import io.inoa.rest.TelemetryVO;
import io.micronaut.configuration.kafka.annotation.KafkaListener;
import io.micronaut.configuration.kafka.annotation.OffsetReset;
import io.micronaut.configuration.kafka.annotation.Topic;
import io.micronaut.core.util.CollectionUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Listener to write messages to InfluxDB.
 *
 * @author sschnabe
 */
@KafkaListener(offsetReset = OffsetReset.EARLIEST, redelivery = true)
@Slf4j
@RequiredArgsConstructor
public class InfluxDBListener {

	private final InfluxDBClient influx;
	private final InfluxDBMetrics metrics;

	@Topic(patterns = "inoa\\.telemetry\\..*")
	void receive(TelemetryVO telemetry) {
		try {

			// prepare

			var tenantId = telemetry.getTenantId();
			var gatewayId = telemetry.getGatewayId();
			MDC.put("tenantId", tenantId);
			MDC.put("gatewayId", gatewayId);
			log.trace("Retrieved: {}", telemetry);

			// write to InfluxDB

			var point = Point
					.measurement("inoa")
					.time(telemetry.getTimestamp(), WritePrecision.MS)
					.addField("value", telemetry.getValue());
			if (CollectionUtils.isNotEmpty(telemetry.getExt())) {
				point.addTags(telemetry.getExt());
			}
			influx.getWriteApiBlocking().writePoint(point
					.addTag("tenant_id", tenantId)
					.addTag("gateway_id", gatewayId)
					.addTag("urn", telemetry.getUrn())
					.addTag("device_id", telemetry.getDeviceId())
					.addTag("type", telemetry.getDeviceType())
					.addTag("sensor", telemetry.getSensor()));
			metrics.incrementSuccess();
		} catch (RuntimeException e) {
			log.error("Failed to write to database", e);
			metrics.incrementFailure();
			throw e;
		} finally {
			MDC.remove("tenantId");
			MDC.remove("gatewayId");
		}
	}
}
