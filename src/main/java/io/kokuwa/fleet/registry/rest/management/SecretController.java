package io.kokuwa.fleet.registry.rest.management;

import java.util.List;
import java.util.UUID;

import io.kokuwa.fleet.registry.domain.Gateway;
import io.kokuwa.fleet.registry.domain.GatewayRepository;
import io.kokuwa.fleet.registry.domain.Secret;
import io.kokuwa.fleet.registry.domain.SecretRepository;
import io.kokuwa.fleet.registry.rest.RestMapper;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.exceptions.HttpStatusException;
import io.reactivex.Single;
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
	public Single<HttpResponse<List<SecretVO>>> getSecrets(UUID gatewayId) {
		return toGateway(gatewayId)
				.flatMapPublisher(gateway -> secretRepository
						.findByGatewayOrderByName(gateway)
						.map(secret -> secret.setGateway(gateway)))
				.map(mapper::toSecret).toList().map(HttpResponse::ok);
	}

	@Override
	public Single<HttpResponse<SecretDetailVO>> getSecret(UUID gatewayId, UUID secretId) {
		return toGateway(gatewayId)
				.flatMapMaybe(gateway -> secretRepository
						.findByGatewayAndExternalId(gateway, secretId)
						.map(secret -> secret.setGateway(gateway)))
				.doOnComplete(() -> {
					log.trace("Secret not found.");
					throw new HttpStatusException(HttpStatus.NOT_FOUND, "Secret not found.");
				})
				.toSingle().map(mapper::toSecretDetail).map(HttpResponse::ok);
	}

	@Override
	public Single<HttpResponse<SecretDetailVO>> createSecret(UUID gatewayId, SecretCreateVO vo) {

		// get gateway

		var gatewaySingle = toGateway(gatewayId);

		// check name for uniqueness

		gatewaySingle = gatewaySingle.flatMap(gateway -> secretRepository.existsByGatewayAndName(gateway, vo.getName())
				.flatMap(exists -> {
					if (exists) {
						throw new HttpStatusException(HttpStatus.CONFLICT, "Already exists.");
					}
					return Single.just(gateway);
				}));

		// create secret

		var entity = new Secret()
				.setEnabled(vo.getEnabled())
				.setName(vo.getName())
				.setType(vo.getType());
		switch (vo.getType()) {
			case RSA:
				var rsa = (SecretCreateRSAVO) vo;
				entity.setPublicKey(rsa.getPublicKey());
				entity.setPrivateKey(rsa.getPrivateKey());
				break;
			case HMAC:
				var hmac = (SecretCreateHmacVO) vo;
				entity.setHmac(hmac.getHmac());
				break;
			default:
				break;
		}
		var secretSingle = gatewaySingle
				.map(entity::setGateway)
				.flatMap(secretRepository::save)
				.doOnSuccess(secret -> log.info("Created secret: {}", secret));

		// return

		return secretSingle.map(mapper::toSecretDetail).map(HttpResponse::created);
	}

	@Override
	public Single<HttpResponse<Object>> deleteSecret(UUID gatewayId, UUID secretId) {
		return toGateway(gatewayId)
				.flatMapMaybe(gateway -> secretRepository.findByGatewayAndExternalId(gateway, secretId))
				.doOnComplete(() -> {
					log.trace("Skip deletion of non existing secret.");
					throw new HttpStatusException(HttpStatus.NOT_FOUND, "Secret not found.");
				})
				.flatMapSingle(secret -> secretRepository.delete(secret).toSingle(() -> {
					log.info("Secret {} deleted.", secret.getName());
					return HttpResponse.noContent();
				}));
	}

	private Single<Gateway> toGateway(UUID gatewayId) {
		return gatewayRepository.findByExternalId(gatewayId)
				.doOnComplete(() -> {
					log.trace("Gateway not found.");
					throw new HttpStatusException(HttpStatus.NOT_FOUND, "Gateway not found.");
				})
				.toSingle();
	}
}
