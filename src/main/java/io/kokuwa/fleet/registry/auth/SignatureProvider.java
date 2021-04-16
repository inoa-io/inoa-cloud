package io.kokuwa.fleet.registry.auth;

import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.inject.Singleton;

import io.kokuwa.fleet.registry.domain.Gateway;
import io.kokuwa.fleet.registry.domain.SecretRepository;
import io.micronaut.security.token.jwt.signature.SignatureConfiguration;
import io.micronaut.security.token.jwt.signature.rsa.RSASignature;
import io.micronaut.security.token.jwt.signature.secret.SecretSignature;
import io.micronaut.security.token.jwt.signature.secret.SecretSignatureConfiguration;
import io.reactivex.Flowable;
import lombok.RequiredArgsConstructor;

/**
 * Provides signature configurations for gateways.
 *
 * @author Stephan Schnabel
 */
public interface SignatureProvider {

	Flowable<SignatureConfiguration> find(Gateway gateway);
}

@Singleton
@RequiredArgsConstructor
class DatabaseSignatureGeneratorConfigurationProvider implements SignatureProvider {

	private final SecretRepository repository;

	@Override
	public Flowable<SignatureConfiguration> find(Gateway gateway) {
		return repository.findByGatewayOrderByName(gateway).map(secret -> {
			switch (secret.getType()) {
				case HMAC:
					var configuration = new SecretSignatureConfiguration(secret.getExternalId().toString());
					configuration.setSecret(Base64.getEncoder().encodeToString(secret.getHmac()));
					configuration.setBase64(true);
					return new SecretSignature(configuration);
				case RSA:
					var key = (RSAPublicKey) KeyFactory
							.getInstance("RSA")
							.generatePublic(new X509EncodedKeySpec(secret.getPublicKey()));
					return new RSASignature(() -> key);
				default:
					return null;
			}
		});
	}
}
