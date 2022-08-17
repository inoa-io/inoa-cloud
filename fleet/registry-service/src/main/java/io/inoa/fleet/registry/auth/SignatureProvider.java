package io.inoa.fleet.registry.auth;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;

import io.inoa.fleet.registry.domain.CredentialRepository;
import io.inoa.fleet.registry.domain.Gateway;
import io.micronaut.security.token.jwt.signature.SignatureConfiguration;
import io.micronaut.security.token.jwt.signature.rsa.RSASignature;
import io.micronaut.security.token.jwt.signature.secret.SecretSignature;
import io.micronaut.security.token.jwt.signature.secret.SecretSignatureConfiguration;
import jakarta.inject.Singleton;
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

	private final CredentialRepository repository;

	@Override
	public List<SignatureConfiguration> find(Gateway gateway) {
		var signatures = new ArrayList<SignatureConfiguration>();

		var credentials = repository.findByGateway(gateway);
		if (credentials.isEmpty()) {
			log.debug("No credentials found");
			return List.of();
		}

		for (var credential : credentials) {

			if (!credential.getEnabled()) {
				log.debug("Credential {} with name {} and type {} is disabled",
						credential.getCredentialId(),
						credential.getName(),
						credential.getType());
				continue;
			}

			switch (credential.getType()) {
				case PSK: {
					var config = new SecretSignatureConfiguration(credential.getName());
					config.setSecret(new String(credential.getValue()));
					signatures.add(new SecretSignature(config));
					break;
				}
				case RSA: {
					try {
						var key = (RSAPublicKey) KeyFactory
								.getInstance("RSA")
								.generatePublic(new X509EncodedKeySpec(credential.getValue()));
						signatures.add(new RSASignature(() -> key));
					} catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
						log.error("Unable to parse public key.", e);
					}
					break;
				}
			}
		}

		if (signatures.isEmpty()) {
			log.debug("No valid signatures found");
		}

		return signatures;
	}
}
