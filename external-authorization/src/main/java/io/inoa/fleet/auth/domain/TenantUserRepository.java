package io.inoa.fleet.auth.domain;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.repository.GenericRepository;

public interface TenantUserRepository extends GenericRepository<TenantUser, Void> {

	List<User> findUserByTenant(Tenant tenant);

	Page<User> findUserByTenant(Tenant tenant, Pageable pageable);

	Optional<TenantUser> findByTenantAndUser(Tenant tenant, User user);

	Optional<Tenant> findTenantByTenantTenantIdAndUserEmail(String tenantId, String email);

	List<Tenant> findTenantByUser(User user);

	@NonNull
	TenantUser save(@Valid @NotNull @NonNull TenantUser entity);
}
