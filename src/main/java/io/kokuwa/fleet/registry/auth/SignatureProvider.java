package io.kokuwa.fleet.registry.auth;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;

import io.kokuwa.fleet.registry.ApplicationProperties;
import io.kokuwa.fleet.registry.domain.CredentialRepository;
import io.kokuwa.fleet.registry.domain.Gateway;
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

	List<SignatureConfiguration> find(Gateway gateway);
}

@Singleton
@Slf4j
@RequiredArgsConstructor
class DatabaseSignatureGeneratorConfigurationProvider implements SignatureProvider {

	private final ApplicationProperties properties;
	private final CredentialRepository repository;

	@Override
	public List<SignatureConfiguration> find(Gateway gateway) {

		var authId = properties.getGateway().getCredentialAuthId();
		var optionalCredential = repository.findByGatewayAndAuthId(gateway, authId);
		if (optionalCredential.isEmpty()) {
			log.debug("No credential for authId '{}' found.", authId);
			return List.of();
		}

		var credential = optionalCredential.get();
		if (!credential.getEnabled()) {
			log.debug("Credential with authId '{}' is disabled.", authId);
			return List.of();
		}
		log.debug("Found credential {} for authId '{}' with type {}.", credential.getCredentialId(), authId,
				credential.getType());

		var signatures = new ArrayList<SignatureConfiguration>();
		for (var secret : credential.getSecrets()) {
			switch (credential.getType()) {
				case PSK: {
					log.trace("Found secret {}.", secret.getSecretId());
					var config = new SecretSignatureConfiguration(secret.getSecretId().toString());
					config.setSecret(new String(secret.getSecret()));
					signatures.add(new SecretSignature(config));
					break;
				}
				case RSA: {
					log.trace("Found secret {}.", secret.getSecretId());
					try {
						var key = (RSAPublicKey) KeyFactory
								.getInstance("RSA")
								.generatePublic(new X509EncodedKeySpec(secret.getPublicKey()));
						signatures.add(new RSASignature(() -> key));
					} catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
						log.error("Unable to parse public key.", e);
					}
					break;
				}
				default:
					log.warn("Credential with authId '{}' has unsupported type {} for gateway auth.",
							authId, credential.getType());
			}
		}
		return signatures;
	}
}
