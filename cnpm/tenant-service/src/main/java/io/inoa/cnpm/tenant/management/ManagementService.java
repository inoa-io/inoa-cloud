package io.inoa.cnpm.tenant.management;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.MDC;

import io.inoa.cnpm.tenant.ApplicationProperties;
import io.inoa.cnpm.tenant.domain.Issuer;
import io.inoa.cnpm.tenant.domain.IssuerRepository;
import io.inoa.cnpm.tenant.domain.Tenant;
import io.inoa.cnpm.tenant.domain.TenantRepository;
import io.inoa.cnpm.tenant.domain.TenantUser;
import io.inoa.cnpm.tenant.domain.TenantUserRepository;
import io.inoa.cnpm.tenant.domain.User;
import io.inoa.cnpm.tenant.domain.UserRepository;
import io.inoa.cnpm.tenant.messaging.MessagingClient;
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

	private final ApplicationProperties properties;
	private final SecurityService security;
	private final TenantRepository tenantRepository;
	private final IssuerRepository issuerRepository;
	private final TenantUserRepository tenantUserRepository;
	private final UserRepository userRepository;
	private final MessagingClient messaging;

	// tenant

	List<Tenant> findTenants() {
		return tenantUserRepository
				.findByUserEmail(getUserEmail())
				.stream()
				.map(TenantUser::getTenant)
				.collect(Collectors.toList());
	}

	Optional<Tenant> findTenant(String tenantId) {
		var email = getUserEmail();
		return tenantRepository
				.findByTenantId(tenantId)
				.filter(tenant -> tenantUserRepository.existsByTenantAndUserEmail(tenant, email));
	}

	public boolean existsTenant(String tenantId) {
		return tenantRepository.existsByTenantId(tenantId);
	}

	Tenant createTenant(Tenant tenant) {
		var user = new User().setEmail(getUserEmail());
		var defaults = properties.getIssuerDefaults();
		tenantRepository.save(tenant).setIssuers(Set.of(issuerRepository.save(new Issuer()
				.setTenant(tenant)
				.setName(defaults.getName())
				.setUrl(defaults.getUrl())
				.setCacheDuration(defaults.getCacheDuration()))));
		messaging.sendCloudEvent(tenant, MessagingClient.ACTION_CREATE);
		log.info("Tenant {} created: {}", tenant.getTenantId(), tenant);
		createUser(tenant, user);
		return tenant;
	}

	Optional<Tenant> updateTenant(String tenantId, TenantUpdateVO vo) {
		return findTenant(tenantId).map(tenant -> {
			var changed = false;

			if (vo.getName() != null) {
				if (tenant.getName().equals(vo.getName())) {
					log.trace("Tenant {}: skip update of name {} because not changed.", tenantId, vo.getName());
				} else {
					log.info("Tenant {}: updated name from {} to {}.", tenantId, tenant.getName(), vo.getName());
					changed = true;
					tenant.setName(vo.getName());
				}
			}

			if (vo.getEnabled() != null) {
				if (tenant.getEnabled() == vo.getEnabled()) {
					log.trace("Tenant {}: skip update of enabled {} because not changed.", tenantId, vo.getEnabled());
				} else {
					log.info("Tenant {}: updated enabled to {}.", tenantId, vo.getEnabled());
					changed = true;
					tenant.setEnabled(vo.getEnabled());
				}
			}

			if (changed) {
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

	// issuers

	Optional<Issuer> findIssuer(Tenant tenant, String name) {
		return tenant.getIssuers().stream().filter(r -> r.getName().equals(name)).findAny();
	}

	public Issuer createIssuer(Tenant tenant, String name, IssuerCreateVO vo) {
		if (issuerRepository.existsByTenantAndUrl(tenant, vo.getUrl())) {
			throw new HttpStatusException(HttpStatus.CONFLICT, "Url already exists.");
		}
		if (findIssuer(tenant, name).isPresent()) {
			throw new HttpStatusException(HttpStatus.CONFLICT, "Name already exists.");
		}
		var issuer = issuerRepository.save(new Issuer()
				.setTenant(tenant)
				.setName(name)
				.setUrl(vo.getUrl())
				.setCacheDuration(vo.getCacheDuration() == null
						? properties.getIssuerDefaults().getCacheDuration()
						: vo.getCacheDuration()));
		log.info("Tenant {} issuer created: {}", tenant.getTenantId(), issuer);
		return issuer;
	}

	public Optional<Issuer> updateIssuer(Tenant tenant, String name, IssuerUpdateVO vo) {
		return findIssuer(tenant, name).map(issuer -> {
			var changed = false;

			if (vo.getUrl() != null) {
				if (issuer.getUrl().equals(vo.getUrl())) {
					log.trace("Tenant {} issuer {}: skip update of name {} because not changed.",
							tenant.getTenantId(), name, vo.getUrl());
				} else {
					if (issuerRepository.existsByTenantAndUrl(tenant, vo.getUrl())) {
						throw new HttpStatusException(HttpStatus.CONFLICT, "Url already exists.");
					}
					log.info("Tenant {} issuer {}: updated url from {} to {}.",
							tenant.getTenantId(), name, issuer.getUrl(), vo.getUrl());
					changed = true;
					issuer.setUrl(vo.getUrl());
				}
			}

			if (vo.getCacheDuration() != null) {
				if (issuer.getCacheDuration().equals(vo.getCacheDuration())) {
					log.trace("Tenant {} issuer {}: skip update of name {} because not changed.",
							tenant.getTenantId(), name, vo.getUrl());
				} else {
					log.info("Tenant {} issuer {}: updated cache from {} to {}.",
							tenant.getTenantId(), name, issuer.getCacheDuration(), vo.getCacheDuration());
					changed = true;
					issuer.setCacheDuration(vo.getCacheDuration());
				}
			}

			if (changed) {
				issuerRepository.update(issuer);
			}

			return issuer;
		});
	}

	public boolean deleteIssuer(Tenant tenant, String name) {
		return findIssuer(tenant, name)
				.map(issuer -> {
					issuerRepository.delete(issuer);
					log.info("Tenant {} issuer {} deleted.", tenant.getTenantId(), name);
					return issuer;
				})
				.isPresent();
	}

	// user

	Page<User> findUsers(Tenant tenant, Optional<String> optionalFilter, Pageable pageable) {
		return optionalFilter
				.map(filter -> "%" + filter.replace("*", "%") + "%")
				.map(filter -> tenantUserRepository.findUserByTenantAndUserEmailIlikeFilter(tenant, filter, pageable))
				.orElseGet(() -> tenantUserRepository.findUserByTenant(tenant, pageable));
	}

	Optional<User> findUser(Tenant tenant, UUID userId) {
		return tenantUserRepository
				.findByTenantAndUserUserId(tenant, userId)
				.map(TenantUser::getUser);
	}

	boolean existsUserByEmail(Tenant tenant, String email) {
		return tenantUserRepository.existsByTenantAndUserEmail(tenant, email);
	}

	User createUser(Tenant tenant, User user) {
		MDC.put("userId", user.setUserId(UUID.randomUUID()).getUserId().toString());
		return userRepository.findByEmail(user.getEmail())
				.map(existingUser -> {
					tenantUserRepository.save(new TenantUser().setTenant(tenant).setUser(existingUser));
					log.info("Tenant {} assigned existing user {}: {}",
							tenant.getTenantId(), existingUser.getUserId(), existingUser);
					return existingUser;
				}).orElseGet(() -> {
					userRepository.save(user);
					tenantUserRepository.save(new TenantUser().setTenant(tenant).setUser(user));
					log.info("Tenant {} user {} created: {}", tenant.getTenantId(), user.getUserId(), user);
					return user;
				});
	}

	boolean deleteUser(Tenant tenant, UUID userId) {
		return tenantUserRepository
				.findByTenantAndUserUserId(tenant, userId)
				.map(assignment -> {
					tenantUserRepository.deleteById(assignment.getId());
					log.info("Tenant {} user {} deleted.", tenant.getTenantId(), userId);
					return assignment;
				})
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
