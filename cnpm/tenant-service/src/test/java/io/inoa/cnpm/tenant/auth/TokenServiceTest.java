package io.inoa.cnpm.tenant.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Paths;
import java.security.KeyPairGenerator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.org.apache.commons.io.FileUtils;

import com.nimbusds.jose.jwk.KeyType;
import com.nimbusds.jose.jwk.KeyUse;

import io.inoa.cnpm.tenant.ApplicationProperties;
import io.micronaut.context.exceptions.BeanInstantiationException;

/**
 * Test for {@link TokenService}.
 *
 * @author Rico Pahlisch
 * @author Stephan Schnabel
 */
public class TokenServiceTest {

	@DisplayName("generateToken")
	@Test
	void generateToken() {
		var jwk = new TokenService(new ApplicationProperties()).retrieveJsonWebKeys().get(0);
		assertEquals(KeyType.RSA, jwk.getKeyType(), "keyType");
		assertEquals(KeyUse.SIGNATURE, jwk.getKeyUse(), "keyUse");
	}

	@DisplayName("readToken: success")
	@Test
	void readToken() throws Exception {
		var keyFile = Paths.get(System.getProperty("java.io.tmpdir")).resolve("junit.key").toAbsolutePath();
		var keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
		FileUtils.writeByteArrayToFile(keyFile.toFile(), keyPair.getPrivate().getEncoded());
		assertTrue(keyFile.toFile().exists(), "file not exists");
		var properties = new ApplicationProperties().setPrivateKey("file://" + keyFile);
		var jwk = new TokenService(properties).retrieveJsonWebKeys().get(0);
		assertEquals(KeyType.RSA, jwk.getKeyType(), "keyType");
		assertEquals(KeyUse.SIGNATURE, jwk.getKeyUse(), "keyUse");
	}

	@DisplayName("readToken: pk not found")
	@Test
	void readTokenNotFound() {
		var keyFile = Paths.get("nope.key").toAbsolutePath();
		assertFalse(keyFile.toFile().exists(), "file exists");
		var properties = new ApplicationProperties().setPrivateKey("file://" + keyFile);
		assertThrows(BeanInstantiationException.class, () -> new TokenService(properties));
	}

	@DisplayName("readToken: pk not valid")
	@Test
	void readTokenInvalid() {
		var keyFile = Paths.get("pom.xml").toAbsolutePath();
		assertTrue(keyFile.toFile().exists(), "file not exists");
		var properties = new ApplicationProperties().setPrivateKey("file://" + keyFile);
		assertThrows(BeanInstantiationException.class, () -> new TokenService(properties));
	}
}
