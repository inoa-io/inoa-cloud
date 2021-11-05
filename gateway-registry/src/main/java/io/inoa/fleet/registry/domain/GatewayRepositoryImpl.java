package io.inoa.fleet.registry.domain;

import java.util.Optional;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;

@JdbcRepository
public abstract class GatewayRepositoryImpl implements GatewayRepository {

	public Page<Gateway> findByTenantFilterd(Tenant tenant, Optional<String> filter, Pageable pageable) {
		return filter.map(value -> findByTenantAndNameIlikeFilter(tenant, processFilter(value), pageable))
				.orElseGet(() -> findByTenant(tenant, pageable));
	}

	public abstract Page<Gateway> findByTenantAndNameIlikeFilter(Tenant tenant, String filter, Pageable pageable);

	private String processFilter(String filter) {
		return "%" + filter.replace("*", "%") + "%";
	}
}
