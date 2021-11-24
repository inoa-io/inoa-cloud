package io.inoa.tenant.auth;

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

import io.micronaut.context.exceptions.BeanInstantiationException;
import io.micronaut.core.io.ResourceResolver;
import io.micronaut.security.token.jwt.signature.jwks.DefaultJwkValidator;
import io.micronaut.security.token.jwt.signature.jwks.JwkValidator;

/**
 * Test for {@link JwtService}.
 *
 * @author Stephan Schnabel
 */
public class JwtServiceTest {

	private final ResourceResolver resolver = new ResourceResolver();
	private final JwkValidator validator = new DefaultJwkValidator();

	@DisplayName("generateToken")
	@Test
	void generateToken() {
		var jwk = new JwtService(new InoaAuthProperties(), validator, resolver).getJwk();
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
		var properties = new InoaAuthProperties().setPrivateKey("file://" + keyFile);
		var jwk = new JwtService(properties, validator, resolver).getJwk();
		assertEquals(KeyType.RSA, jwk.getKeyType(), "keyType");
		assertEquals(KeyUse.SIGNATURE, jwk.getKeyUse(), "keyUse");
	}

	@DisplayName("readToken: pk not found")
	@Test
	void readTokenNotFound() {
		var keyFile = Paths.get("nope.key").toAbsolutePath();
		assertFalse(keyFile.toFile().exists(), "file exists");
		var properties = new InoaAuthProperties().setPrivateKey("file://" + keyFile);
		assertThrows(BeanInstantiationException.class, () -> new JwtService(properties, validator, resolver));
	}

	@DisplayName("readToken: pk not valid")
	@Test
	void readTokenInvalid() {
		var keyFile = Paths.get("pom.xml").toAbsolutePath();
		assertTrue(keyFile.toFile().exists(), "file not exists");
		var properties = new InoaAuthProperties().setPrivateKey("file://" + keyFile);
		assertThrows(BeanInstantiationException.class, () -> new JwtService(properties, validator, resolver));
	}
}
