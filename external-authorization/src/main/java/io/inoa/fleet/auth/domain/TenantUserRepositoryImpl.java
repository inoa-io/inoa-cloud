package io.inoa.fleet.auth.domain;

import java.util.Optional;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;

@JdbcRepository
public abstract class TenantUserRepositoryImpl implements TenantUserRepository {

	public Page<User> findUserForTenant(Tenant tenant, Optional<String> filter, Pageable pageable) {
		return filter.map(value -> findUserByTenantAndUserEmailIlikeFilter(tenant, processFilter(value), pageable))
				.orElseGet(() -> findUserByTenant(tenant, pageable));
	}

	public abstract Page<User> findUserByTenantAndUserEmailIlikeFilter(Tenant tenant, String filter, Pageable pageable);

	private String processFilter(String filter) {
		return "%" + filter.replace("*", "%") + "%";
	}
}
