package io.kokuwa.fleet.registry.rest.management;

import java.util.List;
import java.util.UUID;

import io.kokuwa.fleet.registry.domain.Credential;
import io.kokuwa.fleet.registry.domain.CredentialRepository;
import io.kokuwa.fleet.registry.domain.Gateway;
import io.kokuwa.fleet.registry.domain.GatewayRepository;
import io.kokuwa.fleet.registry.domain.Secret;
import io.kokuwa.fleet.registry.domain.SecretRepository;
import io.kokuwa.fleet.registry.rest.RestMapper;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.exceptions.HttpStatusException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of {@link CredentialsApi}.
 *
 * @author Stephan Schnabel
 */
@Controller
@Slf4j
@RequiredArgsConstructor
public class CredentialsController implements CredentialsApi {

	private final SecurityManagement security;
	private final RestMapper mapper;
	private final GatewayRepository gatewayRepository;
	private final CredentialRepository credentialRepository;
	private final SecretRepository secretRepository;

	@Override
	public HttpResponse<List<CredentialVO>> findCredentials(UUID gatewayId) {
		return HttpResponse.ok(mapper.toCredentials(credentialRepository.findByGateway(getGateway(gatewayId))));
	}

	@Override
	public HttpResponse<CredentialVO> findCredential(UUID gatewayId, UUID credentialId) {
		return HttpResponse.ok(mapper.toCredential(getCredential(gatewayId, credentialId)));
	}

	@Override
	public HttpResponse<SecretDetailVO> findSecret(UUID gatewayId, UUID credentialId, UUID secretId) {
		return HttpResponse.ok(mapper.toSecretDetail(getSecret(gatewayId, credentialId, secretId)));
	}

	@Override
	public HttpResponse<CredentialVO> createCredential(UUID gatewayId, CredentialCreateVO vo) {
		var gateway = getGateway(gatewayId);
		if (credentialRepository.existsByGatewayAndAuthId(gateway, vo.getAuthId())) {
			throw new HttpStatusException(HttpStatus.CONFLICT, "AuthId already exists.");
		}
		var credential = credentialRepository.save(mapper.toCredential(gateway, vo));
		log.info("Created credential: {}", credential);
		return HttpResponse.created(mapper.toCredential(credential));
	}

	@Override
	public HttpResponse<SecretDetailVO> createSecret(UUID gatewayId, UUID credentialId, SecretCreateVO vo) {
		var credential = getCredential(gatewayId, credentialId);
		var secret = secretRepository.save(mapper.toSecret(credential, vo));
		log.info("Created secret: {}", secret);
		return HttpResponse.created(mapper.toSecretDetail(secret));
	}

	@Override
	public HttpResponse<Object> deleteCredential(UUID gatewayId, UUID credentialId) {
		var credential = getCredential(gatewayId, credentialId);
		credentialRepository.delete(credential);
		log.info("Credential {} deleted.", credential.getCredentialId());
		return HttpResponse.noContent();
	}

	@Override
	public HttpResponse<Object> deleteSecret(UUID gatewayId, UUID credentialId, UUID secretId) {
		var secret = getSecret(gatewayId, credentialId, secretId);
		secretRepository.delete(secret);
		log.info("Secret {} deleted.", secret.getSecretId());
		return HttpResponse.noContent();
	}

	private Gateway getGateway(UUID gatewayId) {
		var optional = gatewayRepository.findByTenantAndGatewayId(security.getTenant(), gatewayId);
		if (optional.isEmpty()) {
			throw new HttpStatusException(HttpStatus.NOT_FOUND, "Gateway not found.");
		}
		return optional.get();
	}

	private Credential getCredential(UUID gatewayId, UUID credentialId) {
		var optional = credentialRepository.findByGatewayAndCredentialId(getGateway(gatewayId), credentialId);
		if (optional.isEmpty()) {
			throw new HttpStatusException(HttpStatus.NOT_FOUND, "Credential not found.");
		}
		return optional.get();
	}

	private Secret getSecret(UUID gatewayId, UUID credentialId, UUID secretId) {
		var credential = getCredential(gatewayId, credentialId);
		var optional = secretRepository.findByCredentialAndSecretId(credential, secretId);
		if (optional.isEmpty()) {
			throw new HttpStatusException(HttpStatus.NOT_FOUND, "Secret not found.");
		}
		return optional.get().setCredential(credential);
	}
}
