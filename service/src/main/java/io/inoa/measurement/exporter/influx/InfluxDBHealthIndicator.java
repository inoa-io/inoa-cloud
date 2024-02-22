package io.inoa.measurement.exporter.influx;

import java.util.concurrent.atomic.AtomicReference;

import org.reactivestreams.Publisher;

import com.influxdb.client.InfluxDBClient;

import io.micronaut.context.annotation.Requires;
import io.micronaut.context.env.Environment;
import io.micronaut.core.async.publisher.Publishers;
import io.micronaut.health.HealthStatus;
import io.micronaut.management.health.indicator.HealthIndicator;
import io.micronaut.management.health.indicator.HealthResult;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * A {@link HealthIndicator} for InfluxDB.
 *
 * @author stephan.schnabel@grayc.de
 */
@Requires(notEnv = Environment.TEST)
@Requires(beans = InfluxDBClient.class)
@Singleton
@Slf4j
@RequiredArgsConstructor
public class InfluxDBHealthIndicator implements HealthIndicator {

	private final AtomicReference<HealthStatus> status = new AtomicReference<>();
	private final InfluxDBClient influxdb;

	@Override
	public Publisher<HealthResult> getResult() {
		if (status.get() != HealthStatus.UP) {
			if (influxdb.ready() == null) {
				log.warn("Failed to ping to InfluxDB");
				status.set(HealthStatus.DOWN);
			} else {
				status.set(HealthStatus.UP);
				log.info("InfluxDB connection etablished with version {}", influxdb.version());
			}
		}
		return Publishers.just(HealthResult.builder("influxdb", status.get()).build());
	}
}
