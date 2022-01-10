package io.inoa.cnpm.tenant.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import io.micronaut.data.annotation.Join;
import io.micronaut.data.annotation.Where;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.Sort;
import io.micronaut.data.repository.GenericRepository;

/**
 * Repository for {@link Assignment}.
 */
@JdbcRepository
public interface AssignmentRepository extends GenericRepository<Assignment, Long> {

	@Where("assignment_tenant_.deleted IS NULL")
	@Join(value = "tenant", alias = "assignment_tenant_")
	List<Tenant> findTenantByUserEmail(String email, Sort sort);

	@Where("assignment_tenant_.deleted IS NULL")
	@Join(value = "tenant", alias = "assignment_tenant_")
	@Join(value = "user", alias = "assignment_user_")
	Optional<Assignment> findByTenantTenantIdAndUserEmail(String tenantId, String email);

	@Join(value = "user", alias = "assignment_user_")
	Optional<Assignment> findByTenantAndUserUserId(Tenant tenant, UUID userId);

	@Join(value = "user", alias = "assignment_user_")
	Page<Assignment> findByTenant(Tenant tenant, Pageable pageable);

	@Join(value = "user", alias = "assignment_user_")
	Page<Assignment> findByTenantAndUserEmailIlikeFilter(Tenant tenant, String filter, Pageable pageable);

	Assignment save(Assignment assignment);

	Assignment update(Assignment assignment);

	void delete(Assignment assignment);
}
