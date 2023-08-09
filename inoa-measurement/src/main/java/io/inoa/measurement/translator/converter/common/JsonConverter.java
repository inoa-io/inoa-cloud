package io.inoa.measurement.translator.converter.common;

import java.util.Iterator;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import io.inoa.fleet.model.TelemetryRawVO;
import io.inoa.measurement.ApplicationProperties;
import io.inoa.measurement.model.TelemetryVO;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.inject.Singleton;

/**
 * Value converter for json.
 */
@Singleton
public class JsonConverter extends CommonConverter {

	JsonConverter(ApplicationProperties properties, MeterRegistry meterRegistry) {
		super(properties, meterRegistry, "json");
	}

	@Override
	public Stream<TelemetryVO> convert(TelemetryRawVO raw, String type, String sensor) {
		return toJsonNode(raw.getValue()).stream()
				.flatMap(e -> Stream.iterate(e.fields(), Iterator::hasNext, UnaryOperator.identity())
						.map(Iterator::next))
				.filter(node -> node.getValue().isNumber())
				.map(node -> convert(type, sensor, node.getValue().asDouble())
						.urn(raw.getUrn() + "." + node.getKey())
						.sensor(sensor + "." + node.getKey()));
	}
}
