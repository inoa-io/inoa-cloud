package io.inoa.cnpm.tenant.domain;

import java.util.Optional;

import io.micronaut.data.annotation.Join;
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
public interface TenantRepository extends GenericRepository<Tenant, Long> {

	@Join("issuers")
	@Where("@.deleted IS NULL")
	Optional<Tenant> findByTenantId(String tenantId);

	boolean existsByTenantId(String tenantId);

	Tenant save(Tenant tenant);

	Tenant update(Tenant tenant);

	@Query("UPDATE tenant SET deleted = NOW() WHERE tenant_id = :tenantId")
	void deleteByTenantId(String tenantId);
}
