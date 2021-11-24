package io.inoa.tenant.auth;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.text.ParseException;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import io.micronaut.context.exceptions.BeanInstantiationException;
import io.micronaut.core.io.ResourceResolver;
import io.micronaut.security.token.jwt.signature.jwks.JwkValidator;
import io.micronaut.security.token.jwt.signature.jwks.JwksSignature;
import jakarta.inject.Singleton;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
public class JwtService {

	private final Map<String, JwksSignature> issuers = new HashMap<>();
	private final InoaAuthProperties properties;
	private final JwkValidator jwkValidator;
	private final @Getter JWK jwk;

	public JwtService(InoaAuthProperties properties, JwkValidator jwkValidator, ResourceResolver resourceResolver) {
		this.properties = properties;
		this.jwkValidator = jwkValidator;
		this.jwk = loadPrivateKey(resourceResolver);
	}

	public JWTClaimsSet parseJwtToken(String token) throws JOSEException, ParseException {

		var jwt = SignedJWT.parse(token);
		var claims = jwt.getJWTClaimsSet();

		var remoteJwk = issuers.computeIfAbsent(claims.getIssuer(), i -> new JwksSignature(i, null, jwkValidator));
		if (remoteJwk.verify(jwt)) {
			throw new JOSEException("Signature invalid.");
		}

		return claims;
	}

	public String createJwtToken(JWTClaimsSet claims, String tenant) throws JOSEException {

		var now = Instant.now();
		var jwtHeader = new JWSHeader.Builder(JWSAlgorithm.RS256)
				.keyID(properties.getKeyId())
				.type(JOSEObjectType.JWT)
				.build();
		var jwtPayload = new JWTClaimsSet.Builder()
				.subject(claims.getSubject())
				.claim("uid", claims.getSubject())
				.issuer(properties.getIssuer()).audience(List.of("inoa-cloud"))
				.claim("tenant", tenant).claim("email", claims.getClaim("email"))
				.claim("preferred_username", claims.getClaim("preferred_username"))
				.issueTime(Date.from(now))
				.expirationTime(Date.from(now.plusSeconds(60)))
				.build();
		var jwt = new SignedJWT(jwtHeader, jwtPayload);

		var signer = new RSASSASigner(jwk.toRSAKey());
		jwt.sign(signer);
		return jwt.serialize();
	}

	private JWK loadPrivateKey(ResourceResolver resourceResolver) {

		// no key - test mode

		var privateKey = properties.getPrivateKey();
		if (privateKey == null) {
			log.warn("Generate random key. Do not use in Production!");

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
					.keyUse(KeyUse.SIGNATURE)
					.keyID(properties.getKeyId())
					.build();
		}

		// read key from resource

		log.info("Loading private key from location: {}", privateKey);
		var privateKeyAsStream = resourceResolver.getResourceAsStream(privateKey);
		if (privateKeyAsStream.isEmpty()) {
			var message = "Private key " + privateKey + " not found.";
			log.error(message);
			throw new BeanInstantiationException(message);
		}

		try {
			var bytes = privateKeyAsStream.get().readAllBytes();
			var keyFactory = KeyFactory.getInstance("RSA");
			var key = (RSAPrivateCrtKey) keyFactory.generatePrivate(new PKCS8EncodedKeySpec(bytes));
			var keySpec = new RSAPublicKeySpec(key.getModulus(), key.getPublicExponent());
			return new RSAKey.Builder((RSAPublicKey) keyFactory.generatePublic(keySpec))
					.privateKey(key)
					.keyUse(KeyUse.SIGNATURE)
					.keyID(properties.getKeyId())
					.build();
		} catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
			var message = "Private key " + privateKey + " is not readable.";
			log.error(message, e);
			throw new BeanInstantiationException(message, e);
		}
	}
}
