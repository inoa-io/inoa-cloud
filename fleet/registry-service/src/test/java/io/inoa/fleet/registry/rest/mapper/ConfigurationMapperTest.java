package io.inoa.fleet.registry.rest.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.MethodName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import io.inoa.fleet.registry.domain.ConfigurationDefinition;
import io.inoa.fleet.registry.domain.TenantConfiguration;
import io.inoa.fleet.registry.rest.management.ConfigurationTypeVO;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;

/**
 * Tests for {@link ConfigurationMapper}.
 *
 * @author Stephan Schnabel
 */
@DisplayName("api: mapper")
@TestMethodOrder(MethodName.class)
public class ConfigurationMapperTest {

	private final ConfigurationMapper mapper = new ConfigurationMapperImpl();

	@DisplayName("toConfiguration: valid")
	@Test
	void toConfiguration() {
		var definition = new ConfigurationDefinition().setType(ConfigurationTypeVO.INTEGER);
		var configuration = new TenantConfiguration().setDefinition(definition).setValue("4");
		assertEquals(4, mapper.toConfiguration(configuration).getValue(), "value");
	}

	@DisplayName("toString: url valid")
	@Test
	void toStringUrl() {
		var definition = new ConfigurationDefinition().setType(ConfigurationTypeVO.URL);
		var url = "http://localhost:8080";
		assertString(url, definition, url);
	}

	@DisplayName("toString: url invalid type")
	@Test
	void toStringUrlInvalidType() {
		var definition = new ConfigurationDefinition().setType(ConfigurationTypeVO.URL);
		assertBadRequest("String expected, got: Boolean", definition, true);
	}

	@DisplayName("toString: url invalid syntax")
	@Test
	void toStringUrlInvalidSyntax() {
		var definition = new ConfigurationDefinition().setType(ConfigurationTypeVO.URL);
		assertBadRequest("Invalid url: no protocol: nope", definition, "nope");
	}

	@DisplayName("toString: boolean valid")
	@Test
	void toStringBoolean() {
		var definition = new ConfigurationDefinition().setType(ConfigurationTypeVO.BOOLEAN);
		assertString("true", definition, true);
	}

	@DisplayName("toString: boolean invalid type")
	@Test
	void toStringBooleanInvalidType() {
		var definition = new ConfigurationDefinition().setType(ConfigurationTypeVO.BOOLEAN);
		assertBadRequest("Boolean expected, got: String", definition, "nope");
	}

	@DisplayName("toString: integer valid without restrictions")
	@Test
	void toStringIntegerWithoutRestrictions() {
		var definition = new ConfigurationDefinition().setType(ConfigurationTypeVO.INTEGER);
		assertString("4", definition, 4);
	}

	@DisplayName("toString: integer valid with restrictions")
	@Test
	void toStringIntegerWithRestrictions() {
		var definition = new ConfigurationDefinition().setType(ConfigurationTypeVO.INTEGER).setMinimum(1).setMaximum(5);
		assertString("4", definition, 4);
	}

	@DisplayName("toString: integer invalid type")
	@Test
	void toStringIntegerInvalidType() {
		var definition = new ConfigurationDefinition().setType(ConfigurationTypeVO.INTEGER);
		assertBadRequest("Integer expected, got: String", definition, "nope");
	}

	@DisplayName("toString: integer invalid minimum")
	@Test
	void toStringIntegerInvalidMinimum() {
		var definition = new ConfigurationDefinition().setType(ConfigurationTypeVO.INTEGER).setMinimum(4);
		assertBadRequest("Minimum is 4.", definition, 3);
	}

	@DisplayName("toString: integer invalid maximum")
	@Test
	void toStringIntegerInvalidMaximum() {
		var definition = new ConfigurationDefinition().setType(ConfigurationTypeVO.INTEGER).setMaximum(5);
		assertBadRequest("Maximum is 5.", definition, 6);
	}

	@DisplayName("toString: string valid without restrictions")
	@Test
	void toStringStringWithoutRestrictions() {
		var definition = new ConfigurationDefinition().setType(ConfigurationTypeVO.STRING);
		var string = "abcd";
		assertString(string, definition, string);
	}

	@DisplayName("toString: string valid with restrictions")
	@Test
	void toStringStringWithRestrictions() {
		var definition = new ConfigurationDefinition()
				.setType(ConfigurationTypeVO.STRING)
				.setMinimum(1).setMaximum(5).setPattern("^[a-f]*$");
		var string = "abcd";
		assertString(string, definition, string);
	}

	@DisplayName("toString: string invalid type")
	@Test
	void toStringStringInvalidType() {
		var definition = new ConfigurationDefinition().setType(ConfigurationTypeVO.STRING);
		assertBadRequest("String expected, got: Boolean", definition, true);
	}

	@DisplayName("toString: string invalid minLength")
	@Test
	void toStringIntegerInvalidMinLength() {
		var definition = new ConfigurationDefinition().setType(ConfigurationTypeVO.STRING).setMinimum(4);
		assertBadRequest("MinLength is 4.", definition, "abs");
	}

	@DisplayName("toString: string invalid maxLength")
	@Test
	void toStringIntegerInvalidMaxLength() {
		var definition = new ConfigurationDefinition().setType(ConfigurationTypeVO.STRING).setMaximum(5);
		assertBadRequest("MaxLength is 5.", definition, "abcdef");
	}

	@DisplayName("toString: string invalid pattern")
	@Test
	void toStringIntegerInvalidPattern() {
		var definition = new ConfigurationDefinition().setType(ConfigurationTypeVO.STRING).setPattern("^[a-f]*$");
		assertBadRequest("Pattern ^[a-f]*$ mismatch.", definition, "abcdefg");
	}

	private void assertString(String expected, ConfigurationDefinition definition, Object value) {
		assertEquals(expected, mapper.toString(definition, value), "string");
	}

	private void assertBadRequest(String message, ConfigurationDefinition definition, Object value) {
		try {
			mapper.toString(definition, value);
			fail("no bad request.");
		} catch (HttpStatusException e) {
			assertEquals(HttpStatus.BAD_REQUEST, e.getStatus(), "status");
			assertEquals(message, e.getMessage(), "message");
		}
	}
}
