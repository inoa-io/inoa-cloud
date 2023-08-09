package io.inoa.measurement.exporter.influx;

import java.util.HashMap;
import java.util.Map;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;

/**
 * Metrics for InfluxDB listener.
 *
 * @author sschnabe
 */
@Singleton
@RequiredArgsConstructor
public class InfluxDBMetrics {

	public static final String COUNTER_FAILURE = "inoa_measurement_exporter_influxdb_failure";
	public static final String COUNTER_SUCCESS = "inoa_measurement_exporter_influxdb_success";

	private final MeterRegistry meterRegistry;
	private final Map<String, Counter> counters = new HashMap<>();

	public void incrementSuccess() {
		increment(COUNTER_SUCCESS);
	}

	public void incrementFailure() {
		increment(COUNTER_FAILURE);
	}

	private void increment(String counter) {
		counters.computeIfAbsent(counter, string -> meterRegistry.counter(string)).increment();
	}
}
