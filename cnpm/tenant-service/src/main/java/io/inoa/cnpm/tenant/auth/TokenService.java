package io.inoa.cnpm.tenant.auth;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.KeyFactory;
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
import java.util.Optional;

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

import io.inoa.cnpm.tenant.ApplicationProperties;
import io.inoa.cnpm.tenant.domain.TenantUser;
import io.micronaut.context.annotation.Context;
import io.micronaut.context.exceptions.BeanInstantiationException;
import io.micronaut.core.io.ResourceResolver;
import io.micronaut.security.token.jwt.endpoints.JwkProvider;
import io.micronaut.security.token.jwt.generator.claims.JwtClaimsSetAdapter;
import io.micronaut.security.token.jwt.signature.jwks.DefaultJwkValidator;
import io.micronaut.security.token.jwt.signature.jwks.JwksSignature;
import io.micronaut.security.token.jwt.validator.ExpirationJwtClaimsValidator;
import io.micronaut.security.token.jwt.validator.JwtClaimsValidator;
import io.micronaut.security.token.jwt.validator.NotBeforeJwtClaimsValidator;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * Service JWK handling and JWT exchange.
 *
 * @author Rico Pahlisch
 * @author Stephan Schnabel
 */
@Context
@Slf4j
public class TokenService implements JwkProvider {

	private final ApplicationProperties properties;
	private final Map<String, JwksSignature> jwkSignatures;
	private final JWK jwk;
	private final List<JwtClaimsValidator> jwtClaimsValidator;

	TokenService(ApplicationProperties properties) {
		this.properties = properties;
		this.jwkSignatures = new HashMap<>();
		this.jwk = loadJwk();
		this.jwtClaimsValidator = List.of(new ExpirationJwtClaimsValidator(), new NotBeforeJwtClaimsValidator());
	}

	@Override
	public List<JWK> retrieveJsonWebKeys() {
		return List.of(jwk);
	}

	/**
	 * Parse token and validate signature against issuer.
	 *
	 * @param token Provided token.
	 * @return Optional with claims of provided token.
	 */
	@SneakyThrows(JOSEException.class)
	public Optional<JWTClaimsSet> parse(String token) {

		// parse

		SignedJWT jwt = null;
		JWTClaimsSet claims;
		try {
			jwt = SignedJWT.parse(token);
			claims = jwt.getJWTClaimsSet();
		} catch (ParseException e) {
			log.info("Ignored token because parsing failed: {}", e.getMessage());
			return Optional.empty();
		}

		// validate

		var issuer = claims.getIssuer() + "/protocol/openid-connect/certs";
		try {
			new URL(issuer).toURI();
		} catch (MalformedURLException | URISyntaxException e) {
			log.info("Ignored token with unsupported issuer: {}", issuer);
			return Optional.empty();
		}

		var jwks = jwkSignatures.computeIfAbsent(issuer, i -> new JwksSignature(i, null, new DefaultJwkValidator()));
		if (!jwks.verify(jwt)) {
			log.info("Ignored token with invalid signature.");
			return Optional.empty();
		}
		if (!jwtClaimsValidator.stream().allMatch(v -> v.validate(new JwtClaimsSetAdapter(claims), null))) {
			log.info("Ignored token with invalid content.");
			return Optional.empty();
		}

		return Optional.of(claims);
	}

	/**
	 * Copy claims and set tenant & email.
	 *
	 * @param claims     Claim set from request token.
	 * @param assignment Tenant to user assignemnt to get token for.
	 * @return Signed token.
	 */
	@SneakyThrows(JOSEException.class)
	public SignedJWT exchange(JWTClaimsSet claims, TenantUser assignment) {

		log.info("Exchange token for {}/{} with issuer {}.",
				assignment.getTenant().getTenantId(),
				assignment.getUser().getEmail(),
				claims.getIssuer());

		var now = Instant.now();
		var jwtHeader = new JWSHeader.Builder(JWSAlgorithm.RS256)
				.keyID(properties.getKeyId())
				.type(JOSEObjectType.JWT)
				.build();
		var jwtPayload = new JWTClaimsSet.Builder(claims)
				.claim("tenant", assignment.getTenant().getTenantId())
				.issuer(properties.getIssuer())
				.issueTime(Date.from(now))
				.expirationTime(Date.from(now.plusSeconds(60)))
				.build();
		var jwt = new SignedJWT(jwtHeader, jwtPayload);

		var signer = new RSASSASigner(jwk.toRSAKey());
		jwt.sign(signer);
		return jwt;
	}

	@SneakyThrows(NoSuchAlgorithmException.class)
	private JWK loadJwk() {

		// no key - test mode

		var privateKey = properties.getPrivateKey();
		if (privateKey == null) {
			log.warn("Generate random key. Do not use in Production!");
			var pair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
			return new RSAKey.Builder((RSAPublicKey) pair.getPublic())
					.privateKey((RSAPrivateKey) pair.getPrivate())
					.keyUse(KeyUse.SIGNATURE)
					.keyID(properties.getKeyId())
					.build();
		}

		// read key from resource

		log.info("Loading private key from location: {}", privateKey);
		var privateKeyAsStream = new ResourceResolver().getResourceAsStream(privateKey);
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
		} catch (IOException | InvalidKeySpecException e) {
			var message = "Private key " + privateKey + " is not readable.";
			log.error(message, e);
			throw new BeanInstantiationException(message, e);
		}
	}
}
