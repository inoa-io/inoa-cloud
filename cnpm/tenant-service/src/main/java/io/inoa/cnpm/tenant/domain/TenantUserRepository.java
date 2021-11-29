package io.inoa.cnpm.tenant.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import io.micronaut.data.annotation.Join;
import io.micronaut.data.annotation.Query;
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
@Join(value = "tenant", alias = "tenant_user_tenant_")
@Where("tenant_user_tenant_.deleted IS NULL")
public interface TenantUserRepository extends GenericRepository<TenantUser, Void> {

	List<Tenant> findTenantByUserEmail(String email);

	Optional<TenantUser> findByTenantAndUser(Tenant tenant, User user);

	Optional<Tenant> findTenantByTenantTenantIdAndUserEmail(String tenantId, String email);

	Page<User> findUserByTenant(Tenant tenant, Pageable pageable);

	Page<User> findUserByTenantAndUserEmailIlikeFilter(Tenant tenant, String filter, Pageable pageable);

	Optional<User> findUserByTenantAndUserUserId(Tenant tenant, UUID userId);

	Optional<User> findUserByTenantAndUserEmail(Tenant tenant, String email);

	void save(TenantUser tenantUser);

	@Query("DELETE FROM tenant_user WHERE tenant_id = :tenant AND user_id = :user")
	long delete(long tenant, long user);
}
