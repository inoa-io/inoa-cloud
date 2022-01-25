package io.inoa.cnpm.auth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.nimbusds.jose.jwk.KeyType;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jwt.JWTClaimsSet;

import io.inoa.cnpm.auth.AbstractTest;
import io.inoa.cnpm.auth.ApplicationProperties;
import io.inoa.cnpm.auth.ApplicationProperties.TokenExchange;
import io.micronaut.context.exceptions.BeanInstantiationException;

/**
 * Test for {@link TokenLocalService}.
 */
@DisplayName("service: token local")
public class TokenLocalServiceTest extends AbstractTest {

	@DisplayName("loadKey: generate token")
	@Test
	void loadKeyGenerated() {
		var props = new ApplicationProperties();
		var jwk = new TokenLocalService(props).retrieveJsonWebKeys().get(0);
		assertEquals(KeyType.RSA, jwk.getKeyType(), "keyType");
		assertEquals(KeyUse.SIGNATURE, jwk.getKeyUse(), "keyUse");
		assertEquals(props.getTokenExchange().getKeyId(), jwk.getKeyID(), "keyId");
	}

	@DisplayName("loadKey: read token from file")
	@Test
	void loadKeyFromFile() throws NoSuchAlgorithmException, IOException {
		var keyFile = Paths.get(System.getProperty("java.io.tmpdir")).resolve("junit.key").toAbsolutePath();
		var keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
		Files.write(keyFile, keyPair.getPrivate().getEncoded());
		assertTrue(keyFile.toFile().exists(), "file not exists");
		var props = new ApplicationProperties().setTokenExchange(new TokenExchange().setKeyPath("file://" + keyFile));
		var jwk = new TokenLocalService(props).retrieveJsonWebKeys().get(0);
		assertEquals(KeyType.RSA, jwk.getKeyType(), "keyType");
		assertEquals(KeyUse.SIGNATURE, jwk.getKeyUse(), "keyUse");
	}

	@DisplayName("loadKey: fail with key not found")
	@Test
	void loadKeyFailKeyNotFound() {
		var keyFile = Paths.get("nope.key").toAbsolutePath();
		assertFalse(keyFile.toFile().exists(), "file exists");
		var props = new ApplicationProperties().setTokenExchange(new TokenExchange().setKeyPath("file://" + keyFile));
		assertThrows(BeanInstantiationException.class, () -> new TokenLocalService(props));
	}

	@DisplayName("loadKey: fail with key invalid")
	@Test
	void loadKeyFailKeyInvalid() {
		var keyFile = Paths.get("pom.xml").toAbsolutePath();
		assertTrue(keyFile.toFile().exists(), "file not exists");
		var props = new ApplicationProperties().setTokenExchange(new TokenExchange().setKeyPath("file://" + keyFile));
		assertThrows(BeanInstantiationException.class, () -> new TokenLocalService(props));
	}

	@DisplayName("sign & verify")
	@Test
	void signAndVerify() {
		var service = new TokenLocalService(new ApplicationProperties());
		var signed = service.sign(new JWTClaimsSet.Builder().subject("123").build());
		assertTrue(service.verify(signed), "signed token is not valid");
		var serviceWithOtherKey = new TokenLocalService(new ApplicationProperties());
		assertNotEquals(service.retrieveJsonWebKeys(), serviceWithOtherKey.retrieveJsonWebKeys(), "jwks equals");
		assertFalse(serviceWithOtherKey.verify(signed), "signed token is not valid");
	}
}
