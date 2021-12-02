package io.inoa.cnpm.tenant.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import io.micronaut.data.annotation.Join;
import io.micronaut.data.annotation.Where;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.repository.GenericRepository;

/**
 * Repository for {@link TenantUser}.
 *
 * @author Stephan Schnabel
 */
@JdbcRepository
public interface TenantUserRepository extends GenericRepository<TenantUser, Long> {

	boolean existsByTenantAndUserEmail(Tenant tenant, String email);

	@Where("tenant_user_tenant_.deleted IS NULL")
	@Join(value = "tenant", alias = "tenant_user_tenant_")
	@Join(value = "tenant.issuers", alias = "tenant_user_tenant_issuers_")
	List<TenantUser> findByUserEmail(String email);

	Page<User> findUserByTenant(Tenant tenant, Pageable pageable);

	Page<User> findUserByTenantAndUserEmailIlikeFilter(Tenant tenant, String filter, Pageable pageable);

	Optional<TenantUser> findByTenantAndUser(Tenant tenant, User user);

	@Join(value = "user", alias = "tenant_user_user_")
	Optional<TenantUser> findByTenantAndUserUserId(Tenant tenant, UUID userId);

	void save(TenantUser tenantUser);

	void deleteById(Long id);
}
