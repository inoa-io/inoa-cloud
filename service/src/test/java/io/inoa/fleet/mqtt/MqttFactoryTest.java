package io.inoa.fleet.mqtt;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import jakarta.inject.Inject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.ThrowingSupplier;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.netty.handler.ssl.SslContext;

/**
 * Test for {@link MqttFactory}.
 *
 * @author stephan.schnabel@grayc.de
 */
@DisplayName("mqtt: factory")
@MicronautTest(startApplication = false)
public class MqttFactoryTest extends AbstractMqttTest {

	@Inject
	MqttFactory factory;

	@DisplayName("ssl: with self signed certificate")
	@Test
	void sslContextSelfSigned() {
		var properties = new MqttProperties();
		properties.getTls().setGenerateKey(true);
		assertSslContext(() -> factory.sslContext(properties));
	}

	@DisplayName("ssl: with provided cert")
	@Test
	void sslContextFromFile() {
		var properties = new MqttProperties();
		properties.getTls().setGenerateKey(false);
		properties.getTls().setKey(Path.of("src/test/x509/key.pem"));
		properties.getTls().setCert(Path.of("src/test/x509/cert.pem"));
		assertSslContext(() -> factory.sslContext(properties));
	}

	@DisplayName("ssl: with invalid certificate")
	@Test
	void sslContextFromFileButInvalid() {
		var properties = new MqttProperties();
		properties.getTls().setGenerateKey(false);
		properties.getTls().setCert(Path.of("src/test/x509/key.pem"));
		properties.getTls().setKey(Path.of("src/test/x509/cert.pem"));
		var exception = assertThrows(IllegalArgumentException.class, () -> factory.sslContext(properties));
		assertEquals(
				"File does not contain valid certificates: src/test/x509/key.pem",
				exception.getMessage(),
				"message");
	}

	static void assertSslContext(ThrowingSupplier<SslContext> executable) {
		var context = assertDoesNotThrow(executable, "failed to get ssl context");
		assertTrue(context.isServer(), "context should be server");
	}
}
