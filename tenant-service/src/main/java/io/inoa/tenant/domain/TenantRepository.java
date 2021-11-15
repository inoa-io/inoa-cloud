package io.inoa.tenant.domain;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.repository.GenericRepository;

/**
 * Repository for {@link Tenant}.
 *
 * @author Stephan Schnabel
 */
@JdbcRepository
public interface TenantRepository extends GenericRepository<Tenant, Long> {

	boolean existsByTenantId(String tenantId);

	Tenant save(Tenant tenant);

	Tenant update(Tenant tenant);

	void deleteByTenantId(String tenantId);
}
