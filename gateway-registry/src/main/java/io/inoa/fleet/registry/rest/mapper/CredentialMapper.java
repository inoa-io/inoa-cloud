package io.inoa.fleet.registry.rest.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import io.inoa.fleet.registry.domain.Credential;
import io.inoa.fleet.registry.domain.Gateway;
import io.inoa.fleet.registry.domain.Secret;
import io.inoa.fleet.registry.rest.management.CredentialCreateVO;
import io.inoa.fleet.registry.rest.management.CredentialVO;
import io.inoa.fleet.registry.rest.management.SecretCreatePSKVO;
import io.inoa.fleet.registry.rest.management.SecretCreatePasswordVO;
import io.inoa.fleet.registry.rest.management.SecretCreateRSAVO;
import io.inoa.fleet.registry.rest.management.SecretCreateVO;
import io.inoa.fleet.registry.rest.management.SecretDetailPSKVO;
import io.inoa.fleet.registry.rest.management.SecretDetailPasswordVO;
import io.inoa.fleet.registry.rest.management.SecretDetailRSAVO;
import io.inoa.fleet.registry.rest.management.SecretDetailVO;
import io.inoa.fleet.registry.rest.management.SecretVO;

/**
 * Mapper for {@link Credential} and {@link Secret}.
 *
 * @author Stephan Schnabel
 */
@Mapper
public interface CredentialMapper {

	// credential

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "credentialId", expression = "java(java.util.UUID.randomUUID())")
	@Mapping(target = "enabled", source = "credential.enabled")
	@Mapping(target = "created", ignore = true)
	@Mapping(target = "updated", ignore = true)
	Credential toCredential(Gateway gateway, CredentialCreateVO credential);

	CredentialVO toCredential(Credential credential);

	List<CredentialVO> toCredentials(List<Credential> credentials);

	// secret

	@Mapping(target = "type", source = "credential.type")
	SecretVO toSecret(Secret secret);

	SecretDetailPasswordVO toSecretDetailPassword(Secret secret);

	SecretDetailPSKVO toSecretDetailPSK(Secret secret);

	SecretDetailRSAVO toSecretDetailRSA(Secret secret);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "secretId", expression = "java(java.util.UUID.randomUUID())")
	@Mapping(target = "privateKey", ignore = true)
	@Mapping(target = "publicKey", ignore = true)
	@Mapping(target = "secret", ignore = true)
	@Mapping(target = "created", ignore = true)
	Secret toSecret(Credential credential, SecretCreatePasswordVO secret);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "secretId", expression = "java(java.util.UUID.randomUUID())")
	@Mapping(target = "secret", ignore = true)
	@Mapping(target = "password", ignore = true)
	@Mapping(target = "created", ignore = true)
	Secret toSecret(Credential credential, SecretCreateRSAVO secret);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "secretId", expression = "java(java.util.UUID.randomUUID())")
	@Mapping(target = "privateKey", ignore = true)
	@Mapping(target = "publicKey", ignore = true)
	@Mapping(target = "password", ignore = true)
	@Mapping(target = "created", ignore = true)
	Secret toSecret(Credential credential, SecretCreatePSKVO secret);

	default Secret toSecret(SecretCreateVO secret) {
		return toSecret(null, secret);
	}

	default Secret toSecret(Credential credential, SecretCreateVO secret) {
		switch (secret.getType()) {
			case PSK:
				return toSecret(credential, (SecretCreatePSKVO) secret);
			case RSA:
				return toSecret(credential, (SecretCreateRSAVO) secret);
			case PASSWORD:
				return toSecret(credential, (SecretCreatePasswordVO) secret);
			default:
				throw new IllegalArgumentException("Unsupported type: " + secret.getType());
		}
	}

	default SecretDetailVO toSecretDetail(Secret secret) {
		switch (secret.getCredential().getType()) {
			case PSK:
				return toSecretDetailPSK(secret);
			case RSA:
				return toSecretDetailRSA(secret);
			case PASSWORD:
				return toSecretDetailPassword(secret);
			default:
				throw new IllegalArgumentException("Unsupported type: " + secret.getCredential().getType());
		}
	}
}
