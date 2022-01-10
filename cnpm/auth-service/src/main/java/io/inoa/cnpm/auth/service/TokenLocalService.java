package io.inoa.cnpm.auth.service;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.List;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import io.inoa.cnpm.auth.ApplicationProperties;
import io.inoa.cnpm.auth.ApplicationProperties.TokenExchange;
import io.micronaut.context.annotation.Context;
import io.micronaut.context.exceptions.BeanInstantiationException;
import io.micronaut.core.io.ResourceResolver;
import io.micronaut.security.token.jwt.endpoints.JwkProvider;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * Service read key to sign/verify exchanged tokens.
 */
@Context
@Slf4j
public class TokenLocalService implements JwkProvider {

	private final RSAKey key;
	private final RSASSASigner signer;
	private final RSASSAVerifier verifier;

	@SneakyThrows(JOSEException.class)
	TokenLocalService(ApplicationProperties properties) {
		this.key = loadKey(properties.getTokenExchange());
		this.signer = new RSASSASigner(key);
		this.verifier = new RSASSAVerifier(key);
	}

	/**
	 * Get jwks for /certs endpoint.
	 *
	 * @return List of jwks.
	 */
	@Override
	public List<JWK> retrieveJsonWebKeys() {
		return List.of(key);
	}

	/**
	 * Validate signature.
	 *
	 * @param jwt Signed jwt to validate.
	 * @return <code>true</code> if valid.
	 */
	@SneakyThrows(JOSEException.class)
	public boolean verify(SignedJWT jwt) {
		return jwt.verify(verifier);
	}

	/**
	 * Sign claims.
	 *
	 * @param claims Claims for jwt.
	 * @return Signed jwt.
	 */
	@SneakyThrows(JOSEException.class)
	public SignedJWT sign(JWTClaimsSet claims) {
		var jwtHeader = new JWSHeader.Builder(JWSAlgorithm.RS256)
				.keyID(key.getKeyID())
				.type(JOSEObjectType.JWT)
				.build();
		var jwt = new SignedJWT(jwtHeader, claims);
		jwt.sign(signer);
		return jwt;
	}

	@SneakyThrows(NoSuchAlgorithmException.class)
	private RSAKey loadKey(TokenExchange properties) {

		// no key - test mode

		var privateKeyResource = properties.getKeyPath();
		if (privateKeyResource == null) {
			log.warn("Generate random key. Do not use in Production!");
			var pair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
			return new RSAKey.Builder((RSAPublicKey) pair.getPublic())
					.privateKey((RSAPrivateKey) pair.getPrivate())
					.keyUse(KeyUse.SIGNATURE)
					.keyID(properties.getKeyId())
					.build();
		}

		// read key from resource

		log.info("Loading private key from location: {}", privateKeyResource);
		var privateKeyAsStream = new ResourceResolver().getResourceAsStream(privateKeyResource);
		if (privateKeyAsStream.isEmpty()) {
			var message = "Private key " + privateKeyResource + " not found.";
			log.error(message);
			throw new BeanInstantiationException(message);
		}

		try {
			var bytes = privateKeyAsStream.get().readAllBytes();
			var keyFactory = KeyFactory.getInstance("RSA");
			var privateKey = (RSAPrivateCrtKey) keyFactory.generatePrivate(new PKCS8EncodedKeySpec(bytes));
			var publicKeySpec = new RSAPublicKeySpec(privateKey.getModulus(), privateKey.getPublicExponent());
			return new RSAKey.Builder((RSAPublicKey) keyFactory.generatePublic(publicKeySpec))
					.privateKey(privateKey)
					.keyUse(KeyUse.SIGNATURE)
					.keyID(properties.getKeyId())
					.build();
		} catch (IOException | InvalidKeySpecException e) {
			var message = "Private key " + privateKeyResource + " is not readable.";
			log.error(message, e);
			throw new BeanInstantiationException(message, e);
		}
	}
}
