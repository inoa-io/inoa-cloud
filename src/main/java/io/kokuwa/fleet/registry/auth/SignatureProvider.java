package io.kokuwa.fleet.registry.auth;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.List;

import javax.inject.Singleton;

import io.kokuwa.fleet.registry.domain.Gateway;
import io.kokuwa.fleet.registry.domain.Secret;
import io.kokuwa.fleet.registry.domain.SecretRepository;
import io.micronaut.security.token.jwt.signature.SignatureConfiguration;
import io.micronaut.security.token.jwt.signature.rsa.RSASignature;
import io.micronaut.security.token.jwt.signature.secret.SecretSignature;
import io.micronaut.security.token.jwt.signature.secret.SecretSignatureConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Provides signature configurations for gateways.
 *
 * @author Stephan Schnabel
 */
public interface SignatureProvider {

	SignatureConfiguration find(Gateway gateway);
}

@Slf4j
@Singleton
@RequiredArgsConstructor
class DatabaseSignatureGeneratorConfigurationProvider implements SignatureProvider {

	private final SecretRepository repository;

	@Override
	public SignatureConfiguration find(Gateway gateway) {
		List<Secret> secrets = repository.findByGatewayOrderByName(gateway);
		for (var secret : secrets) {
			switch (secret.getType()) {
				case HMAC :
					var configuration = new SecretSignatureConfiguration(secret.getSecretId().toString());
					configuration.setSecret(Base64.getEncoder().encodeToString(secret.getHmac()));
					configuration.setBase64(true);
					return new SecretSignature(configuration);
				case RSA :
					try {
						var key = (RSAPublicKey) KeyFactory.getInstance("RSA")
								.generatePublic(new X509EncodedKeySpec(secret.getPublicKey()));
						return new RSASignature(() -> key);
					} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
						log.error(e.getMessage());
					}
				default :
			}
		}
		return null;
	}
}
