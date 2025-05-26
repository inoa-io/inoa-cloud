package io.inoa.fleet.exporter;

import java.util.concurrent.atomic.AtomicReference;

import jakarta.inject.Singleton;

import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.influxdb.client.InfluxDBClient;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.env.Environment;
import io.micronaut.core.async.publisher.Publishers;
import io.micronaut.health.HealthStatus;
import io.micronaut.management.health.indicator.HealthIndicator;
import io.micronaut.management.health.indicator.HealthResult;

/**
 * A {@link HealthIndicator} for InfluxDB.
 *
 * @author stephan.schnabel@grayc.de
 */
@Requires(notEnv = Environment.TEST)
@Requires(bean = InfluxDBClient.class)
@Singleton
public class InfluxDBHealthIndicator implements HealthIndicator {

	private static final Logger log = LoggerFactory.getLogger(InfluxDBHealthIndicator.class);

	private final AtomicReference<HealthStatus> status = new AtomicReference<>();
	private final InfluxDBClient influxdb;

	public InfluxDBHealthIndicator(InfluxDBClient influxdb) {
		this.influxdb = influxdb;
	}

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
