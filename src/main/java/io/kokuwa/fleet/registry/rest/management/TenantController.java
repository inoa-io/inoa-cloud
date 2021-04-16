package io.kokuwa.fleet.registry.rest.management;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import io.kokuwa.fleet.registry.domain.GatewayRepository;
import io.kokuwa.fleet.registry.domain.Tenant;
import io.kokuwa.fleet.registry.domain.TenantRepository;
import io.kokuwa.fleet.registry.rest.RestMapper;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.exceptions.HttpStatusException;
import io.reactivex.Single;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of {@link TenantApi}.
 *
 * @author Stephan Schnabel
 */
@Controller
@Slf4j
@RequiredArgsConstructor
public class TenantController implements TenantApi {

	private final RestMapper mapper;
	private final TenantRepository tenantRepository;
	private final GatewayRepository gatewayRepository;

	@Override
	public Single<HttpResponse<List<TenantVO>>> getTenants() {
		return tenantRepository.findAllOrderByName().map(mapper::toTenant).toList().map(HttpResponse::ok);
	}

	@Override
	public Single<HttpResponse<TenantVO>> getTenant(UUID tenantId) {
		return tenantRepository.findByExternalId(tenantId)
				.doOnComplete(() -> {
					log.trace("Tenant not found.");
					throw new HttpStatusException(HttpStatus.NOT_FOUND, "Tenant not found.");
				})
				.toSingle().map(mapper::toTenant).map(HttpResponse::ok);
	}

	@Override
	public Single<HttpResponse<TenantVO>> createTenant(TenantCreateVO vo) {

		// check id/name for uniqueness

		var uniqueSingle = vo.getTenantId() == null
				? tenantRepository.existsByName(vo.getName())
				: tenantRepository.existsByNameOrExternalId(vo.getName(), vo.getTenantId());
		var uniqueCompletable = uniqueSingle
				.doOnSuccess(exists -> {
					if (exists) {
						throw new HttpStatusException(HttpStatus.CONFLICT, "Already exists.");
					}
				})
				.ignoreElement();

		// create tenant

		var tenantSingle = uniqueCompletable.andThen(Single
				.just((Tenant) new Tenant()
						.setName(vo.getName())
						.setEnabled(vo.getEnabled())
						.setExternalId(vo.getTenantId())))
				.flatMap(tenantRepository::save)
				.doOnSuccess(tenant -> log.info("Created tenant: {}", tenant));

		// return

		return tenantSingle.map(mapper::toTenant).map(HttpResponse::created);
	}

	@Override
	public Single<HttpResponse<TenantVO>> updateTenant(UUID tenantId, TenantUpdateVO vo) {

		// get tenant from database

		var changed = new AtomicBoolean(false);
		var tenantSingle = tenantRepository.findByExternalId(tenantId)
				.doOnComplete(() -> {
					log.trace("Skip update of non existing tenant.");
					throw new HttpStatusException(HttpStatus.NOT_FOUND, "Tenant not found.");
				})
				.toSingle();

		// update fields

		if (vo.getName() != null) {
			tenantSingle = tenantSingle.flatMap(tenant -> {
				if (tenant.getName().equals(vo.getName())) {
					log.trace("Tenant {}: skip update of name ecause not changed.", tenant.getName());
					return Single.just(tenant);
				} else {
					return tenantRepository.existsByName(vo.getName()).flatMap(exists -> {
						if (exists) {
							throw new HttpStatusException(HttpStatus.CONFLICT, "Already exists.");
						}
						log.info("Tenant {}: updated name to {}.", tenant.getName(), vo.getName());
						changed.set(true);
						return Single.just(tenant.setName(vo.getName()));
					});
				}
			});
		}
		if (vo.getEnabled() != null) {
			tenantSingle = tenantSingle.flatMap(tenant -> {
				if (tenant.getEnabled() == vo.getEnabled()) {
					log.trace("Skip update of enabled {} because not changed.", tenant.getEnabled());
					return Single.just(tenant);
				} else {
					log.info("Tenant {}: updated enabled to {}.", tenant.getName(), vo.getEnabled());
					changed.set(true);
					return Single.just(tenant.setEnabled(vo.getEnabled()));
				}
			});
		}

		// return updated

		return tenantSingle
				.flatMap(tenant -> changed.get() ? tenantRepository.update(tenant) : Single.just(tenant))
				.map(mapper::toTenant).map(HttpResponse::ok);
	}

	@Override
	public Single<HttpResponse<Object>> deleteTenant(UUID tenantId) {
		return tenantRepository.findByExternalId(tenantId)
				.doOnComplete(() -> {
					log.trace("Skip deletion of non existing tenant.");
					throw new HttpStatusException(HttpStatus.NOT_FOUND, "Tenant not found.");
				})
				.flatMapSingle(tenant -> gatewayRepository.existsByTenant(tenant).flatMap(exists -> {
					if (exists) {
						throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Gateways exists.");
					}
					return tenantRepository.delete(tenant).toSingle(() -> {
						log.info("Tenant {} deleted.", tenant.getName());
						return HttpResponse.noContent();
					});
				}));
	}
}
