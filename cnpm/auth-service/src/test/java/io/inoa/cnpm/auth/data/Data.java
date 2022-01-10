package io.inoa.cnpm.auth.data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import io.inoa.cnpm.tenant.management.IssuerVO;
import io.inoa.cnpm.tenant.management.TenantVO;
import io.inoa.cnpm.tenant.management.UserRoleVO;
import io.inoa.cnpm.tenant.management.UserVO;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;

/**
 * Data for tests.
 */
@Singleton
@RequiredArgsConstructor
public class Data {

	public static final String ISSUER = "0";
	public static final Set<String> SERVICES = Set.of("abc", "def");

	private final HashMap<TenantVO, Set<UserVO>> cache = new HashMap<>();
	private final Security security;

	public void clear() {
		cache.clear();
	}

	public TenantVO addTenant() {
		return addTenant(true, "0");
	}

	public TenantVO addTenant(boolean enabled, String issuer) {
		var tenant = new TenantVO()
				.setTenantId(UUID.randomUUID().toString())
				.setEnabled(enabled)
				.setIssuers(Set.of(new IssuerVO()
						.setName(issuer)
						.setUrl(security.getIssuer(issuer))
						.setServices(SERVICES)));
		cache.put(tenant, new HashSet<>());
		return tenant;
	}

	public UserVO addUser(TenantVO tenant, UserRoleVO role) {
		var user = new UserVO().setUserId(UUID.randomUUID()).setRole(role).setEmail(UUID.randomUUID().toString());
		cache.get(tenant).add(user);
		return user;
	}

	public Optional<TenantVO> findTenant(String tenantId) {
		return cache.keySet().stream()
				.filter(t -> t.getTenantId().equals(tenantId))
				.findAny();
	}

	public Optional<UserVO> findUser(String tenantId, String email) {
		return cache.entrySet().stream()
				.filter(e -> e.getKey().getTenantId().equals(tenantId))
				.flatMap(e -> e.getValue().stream().filter(u -> u.getEmail().equals(email)))
				.findFirst();
	}
}
