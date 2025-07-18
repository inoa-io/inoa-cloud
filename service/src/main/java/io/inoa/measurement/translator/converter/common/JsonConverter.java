package io.inoa.measurement.translator.converter.common;

import java.util.stream.Stream;

import jakarta.inject.Singleton;

import io.inoa.measurement.translator.TranslatorProperties;
import io.inoa.messaging.TelemetryRawVO;
import io.inoa.rest.TelemetryVO;
import io.micrometer.core.instrument.MeterRegistry;

/** Value converter for json. */
@Singleton
public class JsonConverter extends CommonConverter {

	public JsonConverter(TranslatorProperties properties, MeterRegistry meterRegistry) {
		super(properties, meterRegistry, "json");
	}

	@Override
	public Stream<TelemetryVO> convert(TelemetryRawVO raw, String type, String sensor) {
		return toJsonNode(raw.value()).stream()
				.flatMap(e -> e.properties().stream())
				.filter(node -> node.getValue().isNumber())
				.map(node -> convert(type, sensor, node.getValue().asDouble())
						.urn(raw.urn() + "." + node.getKey())
						.sensor(sensor + "." + node.getKey()));
	}
}
