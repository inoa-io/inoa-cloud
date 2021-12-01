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
import java.util.Objects;
import java.util.Optional;

import org.slf4j.MDC;

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

import io.inoa.cnpm.tenant.ApplicationProperties;
import io.inoa.cnpm.tenant.domain.Tenant;
import io.micronaut.context.annotation.Context;
import io.micronaut.context.exceptions.BeanInstantiationException;
import io.micronaut.core.io.ResourceResolver;
import io.micronaut.security.token.jwt.endpoints.JwkProvider;
import io.micronaut.security.token.jwt.generator.claims.JwtClaimsSetAdapter;
import io.micronaut.security.token.jwt.signature.jwks.DefaultJwkValidator;
import io.micronaut.security.token.jwt.signature.jwks.JwksSignature;
import io.micronaut.security.token.jwt.signature.jwks.JwksSignatureConfigurationProperties;
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
	private final List<JwtClaimsValidator> jwtClaimsValidator;
	private final RSAKey key;
	private final RSASSASigner signer;
	private final RSASSAVerifier verifier;

	@SneakyThrows(JOSEException.class)
	TokenService(ApplicationProperties properties) {
		this.properties = properties;
		this.key = loadRSAKey();
		this.signer = new RSASSASigner(key);
		this.verifier = new RSASSAVerifier(key);
		this.jwkSignatures = new HashMap<>();
		this.jwtClaimsValidator = List.of(new ExpirationJwtClaimsValidator(), new NotBeforeJwtClaimsValidator());
	}

	@Override
	public List<JWK> retrieveJsonWebKeys() {
		return List.of(key);
	}

	/**
	 * Parse token.
	 *
	 * @param token Provided token.
	 * @return Optional token with jwt and claims of provided token.
	 */
	public Optional<Token> toToken(String token) {
		try {
			var jwt = SignedJWT.parse(token);
			var claims = jwt.getJWTClaimsSet();
			MDC.put("azp", String.valueOf(claims.getClaim("azp")));
			MDC.put("aud", String.valueOf(claims.getAudience()));
			return Optional.of(new Token(jwt, claims));
		} catch (ParseException e) {
			log.info("Ignored token because parsing failed: {}", e.getMessage());
			return Optional.empty();
		}
	}

	/**
	 * Validate with local signature.
	 *
	 * @param token Token with jwt and claims.
	 * @return <code>true</code> if valid.
	 */
	@SneakyThrows(JOSEException.class)
	public boolean isValidLocalToken(Token token) {
		return Objects.equals(properties.getIssuer(), token.getClaims().getIssuer()) && token.getJwt().verify(verifier);
	}

	/**
	 * Validate with remote signature.
	 *
	 * @param token Token with jwt and claims.
	 * @return <code>true</code> if valid.
	 */
	@SneakyThrows(JOSEException.class)
	public boolean isValidRemoteToken(Token token) {

		var issuer = token.getClaims().getIssuer() + "/protocol/openid-connect/certs";
		try {
			new URL(issuer).toURI();
		} catch (MalformedURLException | URISyntaxException e) {
			log.info("Ignored token with unsupported issuer: {}", issuer);
			return false;
		}

		var jwks = jwkSignatures.computeIfAbsent(issuer, url -> {
			var configuration = new JwksSignatureConfigurationProperties();
			configuration.setUrl(url);
			return new JwksSignature(configuration, new DefaultJwkValidator());
		});
		if (!jwks.verify(token.getJwt())) {
			log.info("Ignored token with invalid signature.");
			return false;
		}
		if (!jwtClaimsValidator.stream().allMatch(v -> v.validate(new JwtClaimsSetAdapter(token.getClaims()), null))) {
			log.info("Ignored token with invalid content.");
			return false;
		}

		return true;
	}

	/**
	 * Copy claims and set tenant & email.
	 *
	 * @param token  Token with claims from request.
	 * @param tenant Tenant to get token for.
	 * @return Signed token.
	 */
	@SneakyThrows(JOSEException.class)
	public SignedJWT exchange(Token token, Tenant tenant) {

		log.debug("Exchange token for with issuer {}.", token.getClaims().getIssuer());

		var jwtHeader = new JWSHeader.Builder(JWSAlgorithm.RS256)
				.keyID(properties.getKeyId())
				.type(JOSEObjectType.JWT)
				.build();
		var jwtPayload = new JWTClaimsSet.Builder(token.getClaims())
				.claim("tenant", tenant.getTenantId())
				.issuer(properties.getIssuer())
				.issueTime(Date.from(Instant.now()))
				.build();
		var jwt = new SignedJWT(jwtHeader, jwtPayload);
		jwt.sign(signer);
		return jwt;
	}

	@SneakyThrows(NoSuchAlgorithmException.class)
	private RSAKey loadRSAKey() {

		// no key - test mode

		var privateKeyResource = properties.getPrivateKey();
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
