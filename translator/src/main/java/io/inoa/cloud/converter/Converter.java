package io.inoa.cloud.converter;

import java.util.stream.Stream;

import io.inoa.cloud.messages.InoaTelemetryMessageVO;
import io.inoa.hono.messages.HonoTelemetryMessageVO;
import io.micronaut.core.annotation.Indexed;
import io.micronaut.core.order.Ordered;

/**
 * Base class for value converters.
 *
 * @author Stephan Schnabel
 */
@Indexed(Converter.class)
public interface Converter extends Ordered {

	// convert methods

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
	 * @param hono   Hono message.
	 * @param type   Device type.
	 * @param sensor Sensor name.
	 * @return List of inoa messages.
	 */
	Stream<InoaTelemetryMessageVO> convert(HonoTelemetryMessageVO hono, String type, String sensor);
}
