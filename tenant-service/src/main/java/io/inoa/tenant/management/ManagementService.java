package io.inoa.tenant.management;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.MDC;

import io.inoa.tenant.domain.Tenant;
import io.inoa.tenant.domain.TenantRepository;
import io.inoa.tenant.domain.TenantUser;
import io.inoa.tenant.domain.TenantUserRepository;
import io.inoa.tenant.domain.User;
import io.inoa.tenant.domain.UserRepository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import io.micronaut.security.utils.SecurityService;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service for accessing objects.
 *
 * @author Stephan Schnabel
 */
@Singleton
@Slf4j
@RequiredArgsConstructor
public class ManagementService {

	private final SecurityService security;
	private final TenantRepository tenantRepository;
	private final TenantUserRepository tenantUserRepository;
	private final UserRepository userRepository;
	private final MessagingClient messaging;

	// tenant

	List<Tenant> findTenants() {
		return tenantUserRepository.findTenantByUserEmail(getUserEmail());
	}

	Optional<Tenant> findTenant(String tenantId) {
		return tenantUserRepository.findTenantByTenantTenantIdAndUserEmail(tenantId, getUserEmail());
	}

	public boolean existsTenant(String tenantId) {
		return tenantRepository.existsByTenantId(tenantId);
	}

	Tenant createTenant(Tenant tenant) {
		tenantRepository.save(tenant);
		messaging.sendCloudEvent(tenant, MessagingClient.ACTION_CREATE);
		log.info("Tenant {} created: {}", tenant.getTenantId(), tenant);
		createUser(tenant, new User().setEmail(getUserEmail()));
		return tenant;
	}

	Optional<Tenant> updateTenant(String tenantId, TenantUpdateVO vo) {
		return findTenant(tenantId).map(tenant -> {
			var changed = new AtomicBoolean(false);

			if (vo.getName() != null) {
				if (tenant.getName().equals(vo.getName())) {
					log.trace("Tenant {}: skip update of name {} because not changed.", tenantId, vo.getName());
				} else {
					log.info("Tenant {}: updated name from {} to {}.", tenantId, tenant.getName(), vo.getName());
					changed.set(true);
					tenant.setName(vo.getName());
				}
			}

			if (vo.getEnabled() != null) {
				if (tenant.getEnabled() == vo.getEnabled()) {
					log.trace("Tenant {}: skip update of enabled {} because not changed.", tenantId, vo.getEnabled());
				} else {
					log.info("Tenant {}: updated enabled to {}.", tenantId, vo.getEnabled());
					changed.set(true);
					tenant.setEnabled(vo.getEnabled());
				}
			}

			if (changed.get()) {
				tenantRepository.update(tenant);
				messaging.sendCloudEvent(tenant, MessagingClient.ACTION_UPDATE);
			}

			return tenant;
		});
	}

	boolean deleteTenant(String tenantId) {
		return findTenant(tenantId)
				.map(tenant -> {
					tenantRepository.deleteByTenantId(tenantId);
					messaging.sendCloudEvent(tenant, MessagingClient.ACTION_DELETE);
					log.info("Tenant {} deleted:  {}", tenantId, tenant);
					return tenant;
				})
				.isPresent();
	}

	// user

	Optional<Page<User>> findUsers(String tenantId, Optional<String> optionalFilter, Pageable pageable) {
		return findTenant(tenantId).map(tenant -> optionalFilter
				.map(filter -> "%" + filter.replace("*", "%") + "%")
				.map(filter -> tenantUserRepository.findUserByTenantAndUserEmailIlikeFilter(tenant, filter, pageable))
				.orElseGet(() -> tenantUserRepository.findUserByTenant(tenant, pageable)));
	}

	Optional<User> findUser(String tenantId, UUID userId) {
		return findTenant(tenantId)
				.flatMap(tenant -> tenantUserRepository.findUserByTenantAndUserUserId(tenant, userId));
	}

	boolean existsUserByEmail(String tenantId, String email) {
		return findTenant(tenantId)
				.flatMap(tenant -> tenantUserRepository.findUserByTenantAndUserEmail(tenant, email))
				.isPresent();
	}

	Optional<User> createUser(String tenantId, User user) {
		return findTenant(tenantId).map(tenant -> createUser(tenant, user));
	}

	User createUser(Tenant tenant, User user) {
		MDC.put("userId", user.setUserId(UUID.randomUUID()).toString());
		return userRepository.findByEmail(user.getEmail())
				.map(existingUser -> {
					tenantUserRepository.save(new TenantUser(tenant, existingUser));
					log.info("Tenant {} assigned existing user {}: {}",
							tenant.getTenantId(), existingUser.getUserId(), existingUser);
					return existingUser;
				}).orElseGet(() -> {
					userRepository.save(user);
					tenantUserRepository.save(new TenantUser(tenant, user));
					log.info("Tenant {} user {} created: {}", tenant.getTenantId(), user.getUserId(), user);
					return user;
				});
	}

	boolean deleteUser(String tenantId, UUID userId) {
		return findTenant(tenantId).flatMap(tenant -> tenantUserRepository
				.findUserByTenantAndUserUserId(tenant, userId)
				.map(user -> {
					tenantUserRepository.delete(tenant.getId(), user.getId());
					log.info("Tenant {} user {} deleted.", tenantId, userId, user);
					return user;
				}))
				.isPresent();
	}

	// internal

	private String getUserEmail() {
		var email = security.getAuthentication().map(auth -> auth.getAttributes().get("email")).map(Object::toString);
		if (email.isEmpty()) {
			log.warn("Retrieved authenticated jwt without email claim.");
			throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "Not authorized. No email claim.");
		}
		return email.get();
	}
}
