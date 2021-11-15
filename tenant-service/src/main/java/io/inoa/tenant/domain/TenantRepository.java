package io.inoa.tenant.domain;

import io.micronaut.data.annotation.Query;
import io.micronaut.data.annotation.Where;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.repository.GenericRepository;

/**
 * Repository for {@link Tenant}.
 *
 * @author Stephan Schnabel
 */
@JdbcRepository
@Where("@.deleted IS NOT NULL")
public interface TenantRepository extends GenericRepository<Tenant, Long> {

	@Query("SELECT count(id) FROM tenant WHERE tenant_id = :tenantId")
	boolean existsByTenantId(String tenantId);

	Tenant save(Tenant tenant);

	Tenant update(Tenant tenant);

	@Query("UPDATE tenant SET deleted = NOW() WHERE tenant_id = :tenantId")
	void deleteByTenantId(String tenantId);
}
