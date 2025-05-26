package io.inoa.fleet.registry.rest.validation;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import jakarta.inject.Singleton;

import io.inoa.rest.ConfigurationDefinitionIntegerVO;
import io.inoa.rest.ConfigurationDefinitionStringVO;
import io.inoa.rest.ConfigurationDefinitionVO;
import io.inoa.rest.ConfigurationTypeVO;
import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.validation.validator.constraints.ConstraintValidator;
import io.micronaut.validation.validator.constraints.ConstraintValidatorContext;

/**
 * Performs additional constraint checks for {@link ConfigurationDefinitionVO}:
 *
 * <ul>
 * <li>string: pattern parseable
 * <li>string: min & max length
 * <li>integer: min & max
 * </ul>
 *
 * @author Stephan Schnabel
 */
@Singleton
public class ConfigurationDefinitionValidator
		implements ConstraintValidator<ConfigurationDefinitionValid, ConfigurationDefinitionVO> {

	@Override
	public boolean isValid(
			ConfigurationDefinitionVO definition,
			AnnotationValue<ConfigurationDefinitionValid> annotation,
			ConstraintValidatorContext context) {

		if (definition.getType() == ConfigurationTypeVO.INTEGER) {

			var integerDefinition = (ConfigurationDefinitionIntegerVO) definition;
			var minimum = integerDefinition.getMinimum();
			var maximum = integerDefinition.getMaximum();

			if (minimum != null && maximum != null && minimum > maximum) {
				context.messageTemplate("minimum cannot be higher than maximum");
				return false;
			}
		}

		if (definition.getType() == ConfigurationTypeVO.STRING) {

			var stringDefinition = (ConfigurationDefinitionStringVO) definition;
			var minLength = stringDefinition.getMinLength();
			var maxLength = stringDefinition.getMaxLength();
			var pattern = stringDefinition.getPattern();

			if (minLength != null && maxLength != null && minLength > maxLength) {
				context.messageTemplate("minLength cannot be higher than maxLength");
				return false;
			}

			if (pattern != null) {
				try {
					Pattern.compile(pattern);
				} catch (PatternSyntaxException e) {
					context.messageTemplate("pattern not valid: " + e.getDescription());
					return false;
				}
			}
		}

		return true;
	}
}
