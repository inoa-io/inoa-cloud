package io.inoa.tenant.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
public interface TenantUserRepository extends GenericRepository<TenantUser, Void> {

	List<Tenant> findTenantByUserEmail(String email);

	Optional<Tenant> findTenantByTenantTenantIdAndUserEmail(String tenantId, String email);

	Page<User> findUserByTenant(Tenant tenant, Pageable pageable);

	Page<User> findUserByTenantAndUserEmailIlikeFilter(Tenant tenant, String filter, Pageable pageable);

	Optional<User> findUserByTenantAndUserUserId(Tenant tenant, UUID userId);

	Optional<User> findUserByTenantAndUserEmail(Tenant tenant, String email);

	void save(TenantUser tenantUser);

	long deleteByTenantAndUser(Tenant tenant, User user);
}
