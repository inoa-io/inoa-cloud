package io.kokuwa.fleet.registry.rest.management;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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
 * Implementation of {@link SecretApi}.
 *
 * @author Stephan Schnabel
 */
@Controller
@Slf4j
@RequiredArgsConstructor
public class SecretController implements SecretApi {

	private final RestMapper mapper;
	private final GatewayRepository gatewayRepository;
	private final SecretRepository secretRepository;

	@Override
	public HttpResponse<List<SecretVO>> getSecrets(UUID gatewayId) {
		Gateway gateway = toGateway(gatewayId);
		List<Secret> secrets = secretRepository.findByGatewayOrderByName(gateway);
		secrets.forEach(s -> s.setGateway(gateway));
		return HttpResponse.ok(secrets.stream().map(mapper::toSecret).collect(Collectors.toList()));
	}

	@Override
	public HttpResponse<SecretDetailVO> getSecret(UUID gatewayId, UUID secretId) {
		Gateway gateway = toGateway(gatewayId);
		Optional<Secret> optionalSecret = secretRepository.findByGatewayAndSecretId(gateway, secretId);
		if (optionalSecret.isEmpty()) {
			log.trace("Secret not found.");
			throw new HttpStatusException(HttpStatus.NOT_FOUND, "Secret not found.");
		}
		optionalSecret.get().setGateway(gateway);
		return HttpResponse.ok(mapper.toSecretDetail(optionalSecret.get()));
	}

	@Override
	public HttpResponse<SecretDetailVO> createSecret(UUID gatewayId, SecretCreateVO vo) {

		// get gateway

		var gateway = toGateway(gatewayId);

		// check name for uniqueness
		Boolean existsByGatewayAndName = secretRepository.existsByGatewayAndName(gateway, vo.getName());

		if (existsByGatewayAndName) {
			throw new HttpStatusException(HttpStatus.CONFLICT, "Already exists.");
		}

		// create secret

		var entity = new Secret().setSecretId(UUID.randomUUID()).setEnabled(vo.getEnabled()).setName(vo.getName())
				.setType(vo.getType());
		switch (vo.getType()) {
			case RSA :
				var rsa = (SecretCreateRSAVO) vo;
				entity.setPublicKey(rsa.getPublicKey());
				entity.setPrivateKey(rsa.getPrivateKey());
				break;
			case HMAC :
				var hmac = (SecretCreateHmacVO) vo;
				entity.setHmac(hmac.getHmac());
				break;
			default :
				break;
		}
		entity.setGateway(gateway);
		var secret = secretRepository.save(entity);
		log.info("Created secret: {}", secret);

		// return
		return HttpResponse.created(mapper.toSecretDetail(secret));
	}

	@Override
	public HttpResponse<Object> deleteSecret(UUID gatewayId, UUID secretId) {
		Optional<Secret> optionalSecret = secretRepository.findByGatewayAndSecretId(toGateway(gatewayId), secretId);
		if (optionalSecret.isEmpty()) {
			log.trace("Skip deletion of non existing secret.");
			throw new HttpStatusException(HttpStatus.NOT_FOUND, "Secret not found.");
		}
		secretRepository.delete(optionalSecret.get());
		log.info("Secret {} deleted.", optionalSecret.get().getName());
		return HttpResponse.noContent();
	}

	private Gateway toGateway(UUID gatewayId) {
		Optional<Gateway> optionalGateway = gatewayRepository.findByGatewayId(gatewayId);
		if (optionalGateway.isEmpty()) {
			log.trace("Gateway not found.");
			throw new HttpStatusException(HttpStatus.NOT_FOUND, "Gateway not found.");
		}
		return optionalGateway.get();
	}
}
