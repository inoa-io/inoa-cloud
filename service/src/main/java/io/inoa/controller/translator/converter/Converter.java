package io.inoa.controller.translator.converter;

import java.util.stream.Stream;

import io.inoa.rest.TelemetryRawVO;
import io.inoa.rest.TelemetryVO;
import io.micronaut.core.annotation.Indexed;
import io.micronaut.core.order.Ordered;

@Indexed(Converter.class)
public interface Converter extends Ordered {

	/**
	 * Supports converter given type with sensor?
	 *
	 * @param type   Device type.
	 * @param sensor Sensor name.
	 * @return List of inoa messages.
	 */
	boolean supports(String type, String sensor);

	/**
	 * Convert hono message to inoa messages.
	 *
	 * @param raw    Raw telemetry from fleet.
	 * @param type   Device type.
	 * @param sensor Sensor name.
	 * @return List of inoa messages.
	 */
	Stream<TelemetryVO> convert(TelemetryRawVO raw, String type, String sensor);
}
