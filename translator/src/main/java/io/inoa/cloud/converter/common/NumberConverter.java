package io.inoa.cloud.converter.common;

import java.util.stream.Stream;

import javax.inject.Singleton;

import io.inoa.cloud.ApplicationProperties;
import io.inoa.cloud.messages.InoaTelemetryMessageVO;
import io.inoa.hono.messages.HonoTelemetryMessageVO;

/**
 * Value converter for {@link Number}.
 *
 * @author Stephan Schnabel
 */
@Singleton
public class NumberConverter extends CommonConverter {

	NumberConverter(ApplicationProperties properties) {
		super(properties, "number");
	}

	@Override
	public Stream<InoaTelemetryMessageVO> convert(HonoTelemetryMessageVO hono, String type, String sensor) {
		return toDouble(hono.getValue()).stream().map(value -> convert(type, sensor, value));
	}
}
