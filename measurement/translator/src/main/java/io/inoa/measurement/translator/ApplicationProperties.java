package io.inoa.measurement.translator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import io.micronaut.context.annotation.ConfigurationProperties;
import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties("inoa.measurement.translator")
@Getter
@Setter
public class ApplicationProperties {

	private Set<TypeProperties> types = new HashSet<>();

	@Getter
	@Setter
	public static class TypeProperties {
		private String name;
		private Set<SensorProperties> sensors = new HashSet<>();
	}

	@Getter
	@Setter
	public static class SensorProperties {
		private String name;
		private String namePattern;
		private String converter;
		private boolean ignore = false;
		private Optional<Double> modifier = Optional.empty();
		private Map<String, String> ext = new HashMap<>();
		private Map<String, Object> config = new HashMap<>();
	}
}
