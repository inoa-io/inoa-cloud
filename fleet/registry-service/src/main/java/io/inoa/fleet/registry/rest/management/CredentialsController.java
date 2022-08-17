package io.inoa.fleet.registry.rest.management;

import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import io.inoa.fleet.registry.domain.Credential;
import io.inoa.fleet.registry.domain.CredentialRepository;
import io.inoa.fleet.registry.domain.Gateway;
import io.inoa.fleet.registry.domain.GatewayRepository;
import io.inoa.fleet.registry.rest.mapper.CredentialMapper;
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

	private final Security security;
	private final CredentialMapper mapper;
	private final GatewayRepository gatewayRepository;
	private final CredentialRepository credentialRepository;

	@Override
	public HttpResponse<List<CredentialVO>> findCredentials(UUID gatewayId) {
		return HttpResponse.ok(mapper.toCredentials(credentialRepository.findByGateway(getGateway(gatewayId))));
	}

	@Override
	public HttpResponse<CredentialVO> findCredential(UUID gatewayId, UUID credentialId) {
		return HttpResponse.ok(mapper.toCredential(getCredential(gatewayId, credentialId)));
	}

	@Override
	public HttpResponse<CredentialVO> createCredential(UUID gatewayId, @Valid CredentialCreateVO vo) {
		var gateway = getGateway(gatewayId);
		if (credentialRepository.existsByGatewayAndName(gateway, vo.getName())) {
			throw new HttpStatusException(HttpStatus.CONFLICT, "Name already exists.");
		}
		var credential = credentialRepository.save(mapper.toCredential(gateway, vo));
		log.info("Created credential: {}", credential);
		return HttpResponse.created(mapper.toCredential(credential));
	}

	@Override
	public HttpResponse<CredentialVO> updateCredential(UUID gatewayId, UUID credentialId,
			@Valid CredentialUpdateVO vo) {
		var credential = getCredential(gatewayId, credentialId);
		var changed = false;

		if (vo.getName() != null) {
			if (credential.getName().equals(vo.getName())) {
				log.trace("Credential {}: skip update of name because not changed.", credential.getName());
			} else {
				if (credentialRepository.existsByGatewayAndName(credential.getGateway(), vo.getName())) {
					throw new HttpStatusException(HttpStatus.CONFLICT, "Name already exists.");
				}
				log.info("Credential {}: updated name to {}.", credential.getCredentialId(), vo.getName());
				changed = true;
				credential.setName(vo.getName());
			}
		}

		if (vo.getEnabled() != null) {
			if (credential.getEnabled() == vo.getEnabled()) {
				log.trace("Skip update of enabled {} because not changed.", credential.getEnabled());
			} else {
				log.info("Credential {}: updated enabled to {}.", credential.getCredentialId(), vo.getEnabled());
				changed = true;
				credential.setEnabled(vo.getEnabled());
			}
		}

		if (changed) {
			credentialRepository.update(credential);
		}
		return HttpResponse.ok(mapper.toCredential(credential));
	}

	@Override
	public HttpResponse<Object> deleteCredential(UUID gatewayId, UUID credentialId) {
		var credential = getCredential(gatewayId, credentialId);
		credentialRepository.delete(credential);
		log.info("Credential {} deleted.", credential.getCredentialId());
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
}
