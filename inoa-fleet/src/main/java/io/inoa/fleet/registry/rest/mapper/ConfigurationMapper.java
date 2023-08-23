package io.inoa.fleet.registry.rest.mapper;

import static io.inoa.fleet.model.ConfigurationTypeVO.BOOLEAN;
import static io.inoa.fleet.model.ConfigurationTypeVO.INTEGER;
import static io.inoa.fleet.model.ConfigurationTypeVO.STRING;
import static io.inoa.fleet.model.ConfigurationTypeVO.URL;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.Named;

import io.inoa.fleet.model.ConfigurationDefinitionBooleanVO;
import io.inoa.fleet.model.ConfigurationDefinitionIntegerVO;
import io.inoa.fleet.model.ConfigurationDefinitionStringVO;
import io.inoa.fleet.model.ConfigurationDefinitionUrlVO;
import io.inoa.fleet.model.ConfigurationDefinitionVO;
import io.inoa.fleet.model.ConfigurationVO;
import io.inoa.fleet.registry.domain.Configuration;
import io.inoa.fleet.registry.domain.ConfigurationDefinition;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;

/**
 * Mapper for {@link ConfigurationDefinition}.
 *
 * @author Stephan Schnabel
 */
@Mapper(componentModel = ComponentModel.JAKARTA)
public interface ConfigurationMapper {

	// definition

	List<ConfigurationDefinitionVO> toDefinitions(List<ConfigurationDefinition> configurationDefinitions);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "tenant", ignore = true)
	@Mapping(target = "minimum", ignore = true)
	@Mapping(target = "maximum", ignore = true)
	@Mapping(target = "pattern", ignore = true)
	ConfigurationDefinition toDefinitionBoolean(ConfigurationDefinitionBooleanVO configurationDefinition);

	@Named("ignore")
	@InheritInverseConfiguration
	ConfigurationDefinitionBooleanVO toDefinitionBoolean(ConfigurationDefinition configurationDefinition);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "tenant", ignore = true)
	@Mapping(target = "minimum", source = "minLength")
	@Mapping(target = "maximum", source = "maxLength")
	ConfigurationDefinition toDefinitionString(ConfigurationDefinitionStringVO configurationDefinition);

	@Named("ignore")
	@InheritInverseConfiguration
	ConfigurationDefinitionStringVO toDefinitionString(ConfigurationDefinition configurationDefinition);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "tenant", ignore = true)
	@Mapping(target = "pattern", ignore = true)
	ConfigurationDefinition toDefinitionInteger(ConfigurationDefinitionIntegerVO configurationDefinition);

	@Named("ignore")
	@InheritInverseConfiguration
	ConfigurationDefinitionIntegerVO toDefinitionInteger(ConfigurationDefinition configurationDefinition);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "tenant", ignore = true)
	@Mapping(target = "minimum", ignore = true)
	@Mapping(target = "maximum", ignore = true)
	@Mapping(target = "pattern", ignore = true)
	ConfigurationDefinition toDefinitionUrl(ConfigurationDefinitionUrlVO configurationDefinition);

	@Named("ignore")
	@InheritInverseConfiguration
	ConfigurationDefinitionUrlVO toDefinitionUrl(ConfigurationDefinition configurationDefinition);

	default ConfigurationDefinitionVO toDefinition(ConfigurationDefinition configurationDefinition) {
		switch (configurationDefinition.getType()) {
			case BOOLEAN:
				return toDefinitionBoolean(configurationDefinition);
			case STRING:
				return toDefinitionString(configurationDefinition);
			case INTEGER:
				return toDefinitionInteger(configurationDefinition);
			case URL:
				return toDefinitionUrl(configurationDefinition);
			default:
				throw new IllegalArgumentException("Unsupported type: " + configurationDefinition.getType());
		}
	}

	default ConfigurationDefinition toDefinition(ConfigurationDefinitionVO configurationDefinition) {
		switch (configurationDefinition.getType()) {
			case BOOLEAN:
				return toDefinitionBoolean((ConfigurationDefinitionBooleanVO) configurationDefinition);
			case STRING:
				return toDefinitionString((ConfigurationDefinitionStringVO) configurationDefinition);
			case INTEGER:
				return toDefinitionInteger((ConfigurationDefinitionIntegerVO) configurationDefinition);
			case URL:
				return toDefinitionUrl((ConfigurationDefinitionUrlVO) configurationDefinition);
			default:
				throw new IllegalArgumentException("Unsupported type: " + configurationDefinition.getType());
		}
	}

	// configuration

	@Mapping(target = "value", expression = "java(toValue(configuration))")
	ConfigurationVO toConfiguration(Configuration configuration);

	default List<ConfigurationVO> toConfigurations(List<? extends Configuration> configurations) {
		return configurations.stream()
				.sorted(Comparator.comparing(Configuration::getDefinition,
						Comparator.comparing(ConfigurationDefinition::getKey)))
				.map(this::toConfiguration)
				.toList();
	}

	default Map<String, Object> toConfigurationMap(List<Configuration> configurations) {
		return configurations.stream().collect(Collectors.toMap(
				configuration -> configuration.getDefinition().getKey(),
				configuration -> toValue(configuration),
				(a, b) -> b, TreeMap::new));
	}

	default Object toValue(Configuration configuration) {
		switch (configuration.getDefinition().getType()) {
			case BOOLEAN:
				return Boolean.parseBoolean(configuration.getValue());
			case INTEGER:
				return Integer.parseInt(configuration.getValue());
			default:
				return configuration.getValue();
		}
	}

	default String toString(ConfigurationDefinition definition, Object object) {

		var minimum = definition.getMinimum();
		var maximum = definition.getMaximum();
		var pattern = definition.getPattern();

		switch (definition.getType()) {

			case BOOLEAN: {
				if (!Boolean.class.isInstance(object)) {
					throw new HttpStatusException(HttpStatus.BAD_REQUEST,
							"Boolean expected, got: " + object.getClass().getSimpleName());
				}
				return String.valueOf(object);
			}

			case INTEGER: {
				if (!Integer.class.isInstance(object)) {
					throw new HttpStatusException(HttpStatus.BAD_REQUEST,
							"Integer expected, got: " + object.getClass().getSimpleName());
				}
				var value = (Integer) object;
				if (minimum != null && value < minimum) {
					throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Minimum is " + minimum + ".");
				}
				if (maximum != null && value > maximum) {
					throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Maximum is " + maximum + ".");
				}
				return value.toString();
			}

			case URL: {
				if (!String.class.isInstance(object)) {
					throw new HttpStatusException(HttpStatus.BAD_REQUEST,
							"String expected, got: " + object.getClass().getSimpleName());
				}
				try {
					return new URL((String) object).toString();
				} catch (MalformedURLException e) {
					throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Invalid url: " + e.getMessage());
				}
			}

			case STRING: {
				if (!String.class.isInstance(object)) {
					throw new HttpStatusException(HttpStatus.BAD_REQUEST,
							"String expected, got: " + object.getClass().getSimpleName());
				}
				var value = (String) object;
				if (minimum != null && value.length() < minimum) {
					throw new HttpStatusException(HttpStatus.BAD_REQUEST, "MinLength is " + minimum + ".");
				}
				if (maximum != null && value.length() > maximum) {
					throw new HttpStatusException(HttpStatus.BAD_REQUEST, "MaxLength is " + maximum + ".");
				}
				if (pattern != null && !Pattern.matches(definition.getPattern(), value)) {
					throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Pattern " + pattern + " mismatch.");
				}
				return value;
			}

			default:
				throw new IllegalArgumentException("Unsupported type: " + definition.getType());
		}
	}
}
