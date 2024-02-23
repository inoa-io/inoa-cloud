package io.inoa.fleet.registry.domain.impl;

import java.util.Optional;

import io.inoa.fleet.registry.domain.Gateway;
import io.inoa.fleet.registry.domain.GatewayRepository;
import io.inoa.fleet.registry.domain.Tenant;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.query.builder.sql.Dialect;

/**
 * Implmentation of {@link GatewayRepository}.
 *
 * @author Stephan Schnabel
 * @author Rico Pahlisch
 */
@JdbcRepository(dialect = Dialect.POSTGRES)
public abstract class GatewayRepositoryImpl implements GatewayRepository {

	@Override
	public Page<Gateway> findByTenant(Tenant tenant, Optional<String> filter, Pageable pageable) {
		return filter
				.map(value -> findByTenantAndNameIlike(tenant, processFilter(value), pageable))
				.orElseGet(() -> findByTenant(tenant, pageable));
	}

	public abstract Page<Gateway> findByTenant(Tenant tenant, Pageable pageable);

	public abstract Page<Gateway> findByTenantAndNameIlike(Tenant tenant, String filter, Pageable pageable);

	private String processFilter(String filter) {
		return "%" + filter.replace("*", "%") + "%";
	}
}
