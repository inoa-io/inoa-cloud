package io.kokuwa.fleet.registry.rest.mapper;

import java.util.List;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.Named;

import io.kokuwa.fleet.registry.domain.ConfigurationDefinition;
import io.kokuwa.fleet.registry.rest.management.ConfigurationDefinitionBooleanVO;
import io.kokuwa.fleet.registry.rest.management.ConfigurationDefinitionIntegerVO;
import io.kokuwa.fleet.registry.rest.management.ConfigurationDefinitionStringVO;
import io.kokuwa.fleet.registry.rest.management.ConfigurationDefinitionUrlVO;
import io.kokuwa.fleet.registry.rest.management.ConfigurationDefinitionVO;

/**
 * Mapper for {@link ConfigurationDefinition}.
 *
 * @author Stephan Schnabel
 */
@Mapper(componentModel = ComponentModel.JSR330)
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
}
