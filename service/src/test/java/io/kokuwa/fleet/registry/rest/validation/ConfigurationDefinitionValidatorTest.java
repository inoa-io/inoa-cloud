package io.kokuwa.fleet.registry.rest.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;

import javax.validation.ClockProvider;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.kokuwa.fleet.registry.rest.management.ConfigurationDefinitionIntegerVO;
import io.kokuwa.fleet.registry.rest.management.ConfigurationDefinitionStringVO;
import io.kokuwa.fleet.registry.rest.management.ConfigurationDefinitionUrlVO;
import io.kokuwa.fleet.registry.rest.management.ConfigurationDefinitionVO;
import io.micronaut.validation.validator.constraints.ConstraintValidator;
import io.micronaut.validation.validator.constraints.ConstraintValidatorContext;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Tests for {@link ConfigurationDefinitionValidator}.
 *
 * @author Stephan Schnabel
 */
@DisplayName("api: validator")
public class ConfigurationDefinitionValidatorTest {

	private final ConstraintValidator<?, ConfigurationDefinitionVO> validator = new ConfigurationDefinitionValidator();

	@DisplayName("valid: url")
	@Test
	void validUrl() {
		var definition = new ConfigurationDefinitionUrlVO();
		var context = new TestConstraintValidatorContext(definition);
		assertTrue(validator.isValid(definition, null, context), "valid");
		assertEquals(Set.of(), context.getMessages(), "messages");
	}

	@DisplayName("valid: boolean")
	@Test
	void validBoolean() {
		var definition = new ConfigurationDefinitionUrlVO();
		var context = new TestConstraintValidatorContext(definition);
		assertTrue(validator.isValid(definition, null, context), "valid");
		assertEquals(Set.of(), context.getMessages(), "messages");
	}

	@DisplayName("valid: string without all fields")
	@Test
	void validStringWithoutFields() {
		var definition = new ConfigurationDefinitionStringVO();
		var context = new TestConstraintValidatorContext(definition);
		assertTrue(validator.isValid(definition, null, context), "valid");
		assertEquals(Set.of(), context.getMessages(), "messages");
	}

	@DisplayName("valid: string without minLength")
	@Test
	void validStringWithoutMinLength() {
		var definition = new ConfigurationDefinitionStringVO().setMaxLength(5).setPattern("a*");
		var context = new TestConstraintValidatorContext(definition);
		assertTrue(validator.isValid(definition, null, context), "valid");
		assertEquals(Set.of(), context.getMessages(), "messages");
	}

	@DisplayName("valid: string without maxLength")
	@Test
	void validStringWithoutMaxLength() {
		var definition = new ConfigurationDefinitionStringVO().setMinLength(5).setPattern("a*");
		var context = new TestConstraintValidatorContext(definition);
		assertTrue(validator.isValid(definition, null, context), "valid");
		assertEquals(Set.of(), context.getMessages(), "messages");
	}

	@DisplayName("valid: string with all fields")
	@Test
	void validStringWithAllFields() {
		var definition = new ConfigurationDefinitionStringVO().setMinLength(5).setMaxLength(5).setPattern("a*");
		var context = new TestConstraintValidatorContext(definition);
		assertTrue(validator.isValid(definition, null, context), "valid");
		assertEquals(Set.of(), context.getMessages(), "messages");
	}

	@DisplayName("invalid: string pattern invalid")
	@Test
	void invalidStringPattern() {
		var definition = new ConfigurationDefinitionStringVO().setPattern("{");
		var context = new TestConstraintValidatorContext(definition);
		assertFalse(validator.isValid(definition, null, context), "valid");
		assertEquals(Set.of("pattern not valid: Illegal repetition"), context.getMessages(), "messages");
	}

	@DisplayName("invalid: string minLength > maxLength")
	@Test
	void invalidStringlength() {
		var definition = new ConfigurationDefinitionStringVO().setMinLength(5).setMaxLength(4);
		var context = new TestConstraintValidatorContext(definition);
		assertFalse(validator.isValid(definition, null, context), "valid");
		assertEquals(Set.of("minLength cannot be higher than maxLength"), context.getMessages(), "messages");
	}

	@DisplayName("valid: integer without all fields")
	@Test
	void validIntegerWithoutFields() {
		var definition = new ConfigurationDefinitionIntegerVO();
		var context = new TestConstraintValidatorContext(definition);
		assertTrue(validator.isValid(definition, null, context), "valid");
		assertEquals(Set.of(), context.getMessages(), "messages");
	}

	@DisplayName("valid: integer without minimum")
	@Test
	void validIntegerWithoutMinLength() {
		var definition = new ConfigurationDefinitionIntegerVO().setMaximum(5);
		var context = new TestConstraintValidatorContext(definition);
		assertTrue(validator.isValid(definition, null, context), "valid");
		assertEquals(Set.of(), context.getMessages(), "messages");
	}

	@DisplayName("valid: integer without maximum")
	@Test
	void validIntegerWithoutMaxLength() {
		var definition = new ConfigurationDefinitionIntegerVO().setMinimum(5);
		var context = new TestConstraintValidatorContext(definition);
		assertTrue(validator.isValid(definition, null, context), "valid");
		assertEquals(Set.of(), context.getMessages(), "messages");
	}

	@DisplayName("valid: integer with all fields")
	@Test
	void validIntegerWithAllFields() {
		var definition = new ConfigurationDefinitionIntegerVO().setMinimum(5).setMaximum(5);
		var context = new TestConstraintValidatorContext(definition);
		assertTrue(validator.isValid(definition, null, context), "valid");
		assertEquals(Set.of(), context.getMessages(), "messages");
	}

	@DisplayName("invalid: integer minimum > maximum")
	@Test
	void invalidIntegerRange() {
		var definition = new ConfigurationDefinitionIntegerVO().setMinimum(5).setMaximum(4);
		var context = new TestConstraintValidatorContext(definition);
		assertFalse(validator.isValid(definition, null, context), "valid");
		assertEquals(Set.of("minimum cannot be higher than maximum"), context.getMessages(), "messages");
	}

	@Getter
	@RequiredArgsConstructor
	private class TestConstraintValidatorContext implements ConstraintValidatorContext {

		private final Object rootBean;
		private final ClockProvider clockProvider = null;
		private final Set<String> messages = new HashSet<>();

		@Override
		public void messageTemplate(String message) {
			messages.add(message);
		}
	}
}