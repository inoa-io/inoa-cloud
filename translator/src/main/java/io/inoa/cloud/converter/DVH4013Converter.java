package io.inoa.cloud.converter;

import java.util.Optional;

import javax.inject.Singleton;

import io.inoa.hono.messages.HonoTelemetryMessageVO;

/**
 * Value converter for DVH4013.
 *
 * @author Stephan Schnabel
 */
@Singleton
public class DVH4013Converter extends ValueConverter {

	public static final String DVH4013 = "dvh4013";

	@Override
	public boolean supports(String deviceType, String sensor) {
		return DVH4013.equals(deviceType);
	}

	@Override
	public Optional<Double> convert(HonoTelemetryMessageVO message) {
		return toDouble(message.getValue());
	}
}
