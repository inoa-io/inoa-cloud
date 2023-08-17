package io.inoa.measurement.translator.converter.common;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

import io.inoa.measurement.ApplicationProperties;
import io.inoa.measurement.model.TelemetryVO;
import io.inoa.measurement.translator.converter.AbstractConverter;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;

/**
 * Base class for common converters with support for supports method.
 */
@RequiredArgsConstructor
public abstract class CommonConverter extends AbstractConverter {

	private final ApplicationProperties properties;
	private final MeterRegistry meterRegistry;
	private final Map<String, Counter> counters = new HashMap<>();
	private final String converter;

	@Override
	public boolean supports(String type, String sensor) {
		return get(type, sensor).isPresent();
	}

	TelemetryVO convert(String type, String sensor, Double value) {
		var telemetry = new TelemetryVO()
				.deviceType(type)
				.sensor(sensor)
				.value(value);
		get(type, sensor).ifPresent(sensorProperties -> {
			if (!sensorProperties.getExt().isEmpty()) {
				telemetry.ext(new HashMap<>(sensorProperties.getExt()));
			}
			sensorProperties.getModifier().ifPresent(modifier -> telemetry.setValue(modifier * value));
		});
		return telemetry;
	}

	Optional<ApplicationProperties.TranslatorProperties.SensorProperties> get(String type, String sensor) {
		return properties.getTranslator().getTypes().stream()
				.filter(typeProperties -> typeProperties.getName().equals(type))
				.flatMap(typeProperties -> typeProperties.getSensors().stream())
				.filter(sensorProperties -> sensorProperties.getConverter().equals(converter))
				.filter(sensorProperties -> Objects.equals(sensorProperties.getName(), sensor)
						|| sensorProperties.getNamePattern() != null
								&& Pattern.matches(sensorProperties.getNamePattern(), sensor))
				.findFirst();
	}

	void increment(String type, String counter) {
		counters.computeIfAbsent(type + counter, string -> meterRegistry.counter(counter, "type", type)).increment();
	}
}
