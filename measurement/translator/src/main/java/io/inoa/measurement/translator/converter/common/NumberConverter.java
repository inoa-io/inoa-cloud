package io.inoa.measurement.translator.converter.common;

import java.util.stream.Stream;

import io.inoa.fleet.telemetry.TelemetryRawVO;
import io.inoa.measurement.telemetry.TelemetryVO;
import io.inoa.measurement.translator.ApplicationProperties;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.inject.Singleton;

/**
 * Value converter for {@link Number}.
 */
@Singleton
public class NumberConverter extends CommonConverter {

	NumberConverter(ApplicationProperties properties, MeterRegistry meterRegistry) {
		super(properties, meterRegistry, "number");
	}

	@Override
	public Stream<TelemetryVO> convert(TelemetryRawVO raw, String type, String sensor) {
		return toDouble(raw.getValue()).stream().map(value -> convert(type, sensor, value));
	}
}
