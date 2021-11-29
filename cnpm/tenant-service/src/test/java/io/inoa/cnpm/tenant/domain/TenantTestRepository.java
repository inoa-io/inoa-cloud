package io.inoa.cnpm.tenant.domain;

import java.util.Optional;

import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.repository.CrudRepository;

/**
 * Test repository for {@link Tenant}.
 *
 * @author Stephan Schnabel
 */
@JdbcRepository
public interface TenantTestRepository extends CrudRepository<Tenant, Long> {

	@Query("SELECT * FROM tenant WHERE tenant_id = :tenantId")
	Optional<Tenant> findByTenantId(String tenantId);
}
