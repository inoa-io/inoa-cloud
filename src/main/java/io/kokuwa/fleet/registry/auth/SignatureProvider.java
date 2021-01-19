package io.kokuwa.fleet.registry.auth;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.function.Function;

import javax.inject.Singleton;

import io.kokuwa.fleet.registry.domain.Gateway;
import io.micronaut.security.token.jwt.signature.SignatureConfiguration;
import io.micronaut.security.token.jwt.signature.secret.SecretSignature;
import io.micronaut.security.token.jwt.signature.secret.SecretSignatureConfiguration;
import io.reactivex.Flowable;

/**
 * Provides signature configurations for gateways.
 *
 * @author Stephan Schnabel
 */
public interface SignatureProvider {

	Flowable<SignatureConfiguration> find(Gateway gateway);
}

@Singleton
class MockSignatureGeneratorConfigurationProvider implements SignatureProvider {

	@Override
	public Flowable<SignatureConfiguration> find(Gateway gateway) {
		Function<String, SecretSignature> s = secret -> {
			var configuration = new SecretSignatureConfiguration(secret);
			configuration.setSecret(Base64.getEncoder().encodeToString(secret.getBytes(StandardCharsets.US_ASCII)));
			configuration.setBase64(true);
			return new SecretSignature(configuration);
		};
		return Flowable.just(
				s.apply("nope_nope_nope_nope_nope_nope_nope"),
				s.apply("pleaseChangeThisSecretForANewOne"));
	}
}
