package io.inoa.cloud.converter;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.inoa.hono.messages.HonoTelemetryMessageVO;
import io.micronaut.core.annotation.Indexed;
import io.micronaut.core.order.Ordered;

/**
 * Base class for value converters.
 *
 * @author Stephan Schnabel
 */
@Indexed(ValueConverter.class)
public abstract class ValueConverter implements Ordered {

	final Logger log = LoggerFactory.getLogger(getClass());

	public abstract boolean supports(String deviceType, String sensor);

	public abstract Optional<Double> convert(HonoTelemetryMessageVO message);

	Optional<Double> toDouble(byte[] data) {
		try {
			return Optional.of(Double.parseDouble(new String(data)));
		} catch (NumberFormatException e) {
			log.debug("Failed to parse number {}.", new String(data));
			return Optional.empty();
		}
	}
}
