package io.inoa.fleet.registry.auth;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;

import io.inoa.fleet.registry.ApplicationProperties;
import io.inoa.fleet.registry.ApplicationProperties.RegistryAuthProperties.RegistryAuthRSA;
import io.micronaut.context.exceptions.BeanInstantiationException;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Store for RSA keys to sign and validate issued tokens.
 *
 * @author Stephan Schnabel
 */
@Singleton
@Slf4j
@RequiredArgsConstructor
public class AuthTokenKeys {

	private final ApplicationProperties properties;
	private final List<RSAKey> keys = new ArrayList<>();
	private JWKSet jwkSet;

	@PostConstruct
	public void init() {
		var auth = properties.getAuth();
		if (auth.getKeys().isEmpty()) {
			if (auth.isGenerateKey()) {
				log.warn("Generate random key. Do not use in Production!");
				keys.add(generateKey());
			} else {
				throw new BeanInstantiationException("No keys provided.");
			}
		} else {
			auth.getKeys().forEach(key -> keys.add(getKey(key)));
		}
		jwkSet = new JWKSet(keys.stream().map(RSAKey::toPublicJWK).collect(Collectors.toList()));
	}

	public RSAKey getSigningKey() {
		return keys.iterator().next();
	}

	public Optional<RSAKey> getKey(String keyId) {
		return keys.stream().filter(k -> k.getKeyID().equals(keyId)).findAny();
	}

	public JWKSet getJwkSet() {
		return jwkSet;
	}

	// internal

	private RSAKey generateKey() {

		KeyPair pair;
		try {
			var generator = KeyPairGenerator.getInstance("RSA");
			generator.initialize(2048);
			pair = generator.generateKeyPair();
		} catch (NoSuchAlgorithmException e) {
			throw new BeanInstantiationException("Unable to generate key.", e);
		}

		return new RSAKey.Builder((RSAPublicKey) pair.getPublic())
				.privateKey((RSAPrivateKey) pair.getPrivate())
				.keyID("random-" + Instant.now().toString())
				.keyUse(KeyUse.SIGNATURE)
				.build();
	}

	private RSAKey getKey(RegistryAuthRSA rsa) {

		RSAPublicKey publicKey;
		RSAPrivateKey privateKey;
		try {
			var factory = KeyFactory.getInstance("RSA");
			publicKey = (RSAPublicKey) factory.generatePublic(new X509EncodedKeySpec(rsa.getPublicKey()));
			privateKey = (RSAPrivateKey) factory.generatePrivate(new PKCS8EncodedKeySpec(rsa.getPrivateKey()));
		} catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
			throw new BeanInstantiationException("Unable to parse key " + rsa.getId() + ".", e);
		}

		return new RSAKey.Builder(publicKey)
				.privateKey(privateKey)
				.keyID(rsa.getId())
				.keyUse(KeyUse.SIGNATURE)
				.build();
	}
}
