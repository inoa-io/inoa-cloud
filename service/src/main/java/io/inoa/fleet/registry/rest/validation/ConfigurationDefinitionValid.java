package io.inoa.fleet.registry.rest.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.inoa.fleet.registry.domain.ConfigurationDefinition;
import jakarta.validation.Constraint;

/**
 * Constraint annotation for {@link ConfigurationDefinition}.
 *
 * @author Stephan Schnabel
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ConfigurationDefinitionValidator.class)
public @interface ConfigurationDefinitionValid {

	String message() default "definition is invalid";
}
