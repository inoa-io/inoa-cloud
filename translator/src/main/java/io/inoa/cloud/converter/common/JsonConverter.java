package io.inoa.cloud.converter.common;

import java.util.Iterator;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import io.inoa.cloud.ApplicationProperties;
import io.inoa.cloud.messages.InoaTelemetryMessageVO;
import io.inoa.hono.messages.HonoTelemetryMessageVO;
import jakarta.inject.Singleton;

/**
 * Value converter for json.
 *
 * @author Stephan Schnabel
 */
@Singleton
public class JsonConverter extends CommonConverter {

	JsonConverter(ApplicationProperties properties) {
		super(properties, "json");
	}

	@Override
	public Stream<InoaTelemetryMessageVO> convert(HonoTelemetryMessageVO hono, String type, String sensor) {
		return toJsonNode(hono.getValue()).stream().flatMap(e -> Stream
				.iterate(e.fields(), Iterator::hasNext, UnaryOperator.identity()).map(Iterator::next))
				.filter(node -> node.getValue().isNumber())
				.map(node -> convert(type, sensor, node.getValue().asDouble())
						.setUrn(hono.getUrn() + "." + node.getKey())
						.setSensor(sensor + "." + node.getKey()));
	}
}
