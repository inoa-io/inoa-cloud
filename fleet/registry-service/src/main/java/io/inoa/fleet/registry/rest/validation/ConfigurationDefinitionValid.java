package io.inoa.fleet.registry.rest.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;

import io.inoa.fleet.registry.domain.ConfigurationDefinition;

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
