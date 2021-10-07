package io.inoa.cloud.converter.common;

import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

import io.inoa.cloud.ApplicationProperties;
import io.inoa.cloud.ApplicationProperties.SensorProperties;
import io.inoa.cloud.converter.AbstractConverter;
import io.inoa.cloud.messages.InoaTelemetryMessageVO;
import lombok.RequiredArgsConstructor;

/**
 * Base class for common converters with support for supports method.
 *
 * @author Stephan Schnabel
 */
@RequiredArgsConstructor
public abstract class CommonConverter extends AbstractConverter {

	private final ApplicationProperties properties;
	private final String converter;

	@Override
	public boolean supports(String type, String sensor) {
		return get(type, sensor).isPresent();
	}

	InoaTelemetryMessageVO convert(String type, String sensor, Double value) {
		var inoa = new InoaTelemetryMessageVO()
				.setDeviceType(type)
				.setSensor(sensor)
				.setValue(value);
		get(type, sensor).ifPresent(sensorProperties -> {
			if (!sensorProperties.getExt().isEmpty()) {
				inoa.setExt(new HashMap<>(sensorProperties.getExt()));
			}
			sensorProperties.getModifier().ifPresent(modifier -> inoa.setValue(modifier * value));
		});
		return inoa;
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
