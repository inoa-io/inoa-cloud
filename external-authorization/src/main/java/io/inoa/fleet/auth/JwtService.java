package io.inoa.fleet.auth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.jwt.SignedJWT;

import io.micronaut.context.exceptions.BeanInstantiationException;
import io.micronaut.core.io.IOUtils;
import io.micronaut.core.io.ResourceResolver;
import jakarta.inject.Singleton;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
public class JwtService {

	private final Map<String, AuthenticationHelper> authenticationManagers = new ConcurrentHashMap<>();
	@Getter
	private final JWK jwk;
	private final InoaAuthProperties properties;

	public JwtService(InoaAuthProperties properties, ResourceResolver resourceResolver) {
		this.properties = properties;
		this.jwk = loadPrivateKey(resourceResolver);
	}

	public String getKeyID() {
		return properties.getKeyId();
	}

	public Jwt parseJwtToken(String token) throws ParseException {
		String issuer = JWTParser.parse(token).getJWTClaimsSet().getIssuer();

		AuthenticationHelper authenticationHelper = this.authenticationManagers.computeIfAbsent(issuer, k -> {
			JwtDecoder jwtDecoder = JwtDecoders.fromIssuerLocation(k);
			return new AuthenticationHelper(jwtDecoder);
		});
		return authenticationHelper.authenticate(token);
	}

	public String createJwtToken(Jwt auth, String tenant) throws JOSEException {
		JWTClaimsSet claimsSet = new JWTClaimsSet.Builder().subject(auth.getClaim("sub"))
				.claim("uid", auth.getClaim("sub")).issuer(properties.getIssuer())
				.audience(List.of("inoa-cloud")).claim("tenant", tenant).claim("email", auth.getClaim("email"))
				.claim("preferred_username", auth.getClaim("preferred_username")).issueTime(new Date())
				.expirationTime(new Date(new Date().getTime() + 60 * 1000)).build();

		SignedJWT signedJWT = new SignedJWT(
				new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(this.getKeyID()).type(JOSEObjectType.JWT).build(),
				claimsSet);
		JWSSigner signer = this.getJWSSigner();
		// Compute the RSA signature
		signedJWT.sign(signer);
		return signedJWT.serialize();
	}

	private JWSSigner getJWSSigner() throws JOSEException {
		return new RSASSASigner(jwk.toRSAKey());
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
			var reader = new BufferedReader(new InputStreamReader(privateKeyAsStream.get()));
			JWK jwk = JWK.parseFromPEMEncodedObjects(IOUtils.readText(reader));
			return new RSAKey.Builder((RSAPublicKey) jwk.toRSAKey().toPublicKey())
					.privateKey((RSAPrivateKey) jwk.toRSAKey().toPrivateKey())
					.keyUse(KeyUse.SIGNATURE)
					.keyID(properties.getKeyId())
					.build();
		} catch (JOSEException | IOException e) {
			var message = "Private key " + privateKey + " is not readable.";
			log.error(message, e);
			throw new BeanInstantiationException(message, e);
		}
	}
}
