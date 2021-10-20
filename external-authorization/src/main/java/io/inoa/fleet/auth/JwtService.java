package io.inoa.fleet.auth;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Singleton;

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
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.jwt.SignedJWT;

import io.micronaut.core.io.IOUtils;
import io.micronaut.core.io.ResourceResolver;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
public class JwtService {

	private JWK jwk;
	private final InoaAuthProperties inoaAuthProperties;
	private final Map<String, AuthenticationHelper> authenticationManagers = new ConcurrentHashMap<>();

	public JwtService(InoaAuthProperties inoaAuthProperties, ResourceResolver resourceResolver) {
		this.inoaAuthProperties = inoaAuthProperties;
		try {
			log.info("loading private key from location: {}", inoaAuthProperties.getPrivateKey());
			Optional<InputStream> resourceAsStream = resourceResolver
					.getResourceAsStream(inoaAuthProperties.getPrivateKey());
			if (resourceAsStream.isPresent()) {
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(resourceAsStream.get()));
				String content = IOUtils.readText(bufferedReader);
				jwk = JWK.parseFromPEMEncodedObjects(content);
			} else {
				log.warn("file not found: {}", inoaAuthProperties.getPrivateKey());
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	JWSSigner getJWSSigner() throws JOSEException {
		return new RSASSASigner(jwk.toRSAKey());
	}

	public String getKeyID() {
		return "test";
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
				.claim("uid", auth.getClaim("sub")).issuer(inoaAuthProperties.getIssuer())
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
}
