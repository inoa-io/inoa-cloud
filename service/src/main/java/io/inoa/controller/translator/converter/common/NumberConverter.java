package io.inoa.controller.translator.converter.common;

import java.util.stream.Stream;

import io.inoa.controller.translator.TranslatorProperties;
import io.inoa.rest.TelemetryRawVO;
import io.inoa.rest.TelemetryVO;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.inject.Singleton;

/**
 * Value converter for {@link Number}.
 */
@Singleton
public class NumberConverter extends CommonConverter {

	public NumberConverter(TranslatorProperties properties, MeterRegistry meterRegistry) {
		super(properties, meterRegistry, "number");
	}

	@Override
	public Stream<TelemetryVO> convert(TelemetryRawVO raw, String type, String sensor) {
		return toDouble(raw.getValue()).stream().map(value -> convert(type, sensor, value));
	}
}
