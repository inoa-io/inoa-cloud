package io.inoa.measurement.translator.converter.common;

import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

import io.inoa.measurement.telemetry.TelemetryVO;
import io.inoa.measurement.translator.ApplicationProperties;
import io.inoa.measurement.translator.ApplicationProperties.SensorProperties;
import io.inoa.measurement.translator.converter.AbstractConverter;
import lombok.RequiredArgsConstructor;

/**
 * Base class for common converters with support for supports method.
 */
@RequiredArgsConstructor
public abstract class CommonConverter extends AbstractConverter {

	private final ApplicationProperties properties;
	private final String converter;

	@Override
	public boolean supports(String type, String sensor) {
		return get(type, sensor).isPresent();
	}

	TelemetryVO convert(String type, String sensor, Double value) {
		var telemetry = new TelemetryVO()
				.setDeviceType(type)
				.setSensor(sensor)
				.setValue(value);
		get(type, sensor).ifPresent(sensorProperties -> {
			if (!sensorProperties.getExt().isEmpty()) {
				telemetry.setExt(new HashMap<>(sensorProperties.getExt()));
			}
			sensorProperties.getModifier().ifPresent(modifier -> telemetry.setValue(modifier * value));
		});
		return telemetry;
	}

	Optional<SensorProperties> get(String type, String sensor) {
		return properties.getTypes().stream()
				.filter(typeProperties -> typeProperties.getName().equals(type))
				.flatMap(typeProperties -> typeProperties.getSensors().stream())
				.filter(sensorProperties -> sensorProperties.getConverter().equals(converter))
				.filter(sensorProperties -> Objects.equals(sensorProperties.getName(), sensor)
						|| sensorProperties.getNamePattern() != null
								&& Pattern.matches(sensorProperties.getNamePattern(), sensor))
				.findFirst();
	}
}
