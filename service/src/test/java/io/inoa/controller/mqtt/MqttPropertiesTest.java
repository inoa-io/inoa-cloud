package io.inoa.controller.mqtt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link MqttProperties}.
 *
 * @author stephan.schnabel@grayc.de
 */
@DisplayName("mqtt: properties validation")
public class MqttPropertiesTest extends AbstractMqttTest {

	@DisplayName("tls with generate key")
	@Test
	void tlsGenerateKey() {
		var properties = new MqttProperties();
		properties.getTls().setGenerateKey(true);
		var violations = validator.validate(properties);
		assertTrue(violations.isEmpty(), "no violations expected, got: " + violations);
	}

	@DisplayName("tls with generate key and cert/key")
	@Test
	void tlsGenerateKeyAndCertKey() {
		var properties = new MqttProperties();
		properties.getTls().setGenerateKey(true);
		properties.getTls().setKey(Path.of("src/test/x509/key.pem"));
		properties.getTls().setCert(Path.of("src/test/x509/cert.pem"));
		var violations = validator.validate(properties);
		assertTrue(violations.isEmpty(), "no violations expected, got: " + violations);
	}

	@DisplayName("tls without any config")
	@Test
	void tlsEmpty() {
		var properties = new MqttProperties();
		var violations = validator.validate(properties);
		assertEquals(1, violations.size(), "violation expected: " + violations);
	}

	@DisplayName("tls with cert/key")
	@Test
	void tlsWithCertKey() {
		var properties = new MqttProperties();
		properties.getTls().setKey(Path.of("src/test/x509/key.pem"));
		properties.getTls().setCert(Path.of("src/test/x509/cert.pem"));
		var violations = validator.validate(properties);
		assertTrue(violations.isEmpty(), "no violations expected, got: " + violations);
	}

	@DisplayName("tls with key")
	@Test
	void tlsWithKey() {
		var properties = new MqttProperties();
		properties.getTls().setKey(Path.of("src/test/x509/key.pem"));
		var violations = validator.validate(properties);
		assertEquals(1, violations.size(), "violation expected: " + violations);
	}

	@DisplayName("tls with cert")
	@Test
	void tlsWithCert() {
		var properties = new MqttProperties();
		properties.getTls().setCert(Path.of("src/test/x509/cert.pem"));
		var violations = validator.validate(properties);
		assertEquals(1, violations.size(), "violation expected: " + violations);
	}

	@DisplayName("tls with key not found")
	@Test
	void tlsWithCertNotFound() {
		var properties = new MqttProperties();
		properties.getTls().setKey(Path.of("src/test/x509/key.pem"));
		properties.getTls().setCert(Path.of("nope"));
		var violations = validator.validate(properties);
		assertEquals(1, violations.size(), "violation expected: " + violations);
	}

	@DisplayName("tls with key not found")
	@Test
	void tlsWithKeyNotFound() {
		var properties = new MqttProperties();
		properties.getTls().setKey(Path.of("nope"));
		properties.getTls().setCert(Path.of("src/test/x509/cert.pem"));
		var violations = validator.validate(properties);
		assertEquals(1, violations.size(), "violation expected: " + violations);
	}

	@DisplayName("ports valid")
	@Test
	void portsValid() {
		var properties = new MqttProperties();
		properties.getTls().setGenerateKey(true);
		var violations = validator.validate(properties);
		assertTrue(violations.isEmpty(), "no violations expected, got: " + violations);
	}

	@DisplayName("ports same")
	@Test
	void portsSame() {
		var properties = new MqttProperties();
		properties.setPort(123);
		properties.getTls().setGenerateKey(true);
		properties.getTls().setPort(123);
		var violations = validator.validate(properties);
		assertEquals(1, violations.size(), "violation expected: " + violations);
	}
}
