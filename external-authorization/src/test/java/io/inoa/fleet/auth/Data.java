package io.inoa.fleet.auth;

import java.util.UUID;

import javax.transaction.Transactional;

import io.inoa.fleet.auth.domain.Tenant;
import io.inoa.fleet.auth.domain.TenantRepository;
import io.inoa.fleet.auth.domain.User;
import io.inoa.fleet.auth.domain.UserRepository;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;

@Singleton
@Transactional(Transactional.TxType.REQUIRES_NEW)
@RequiredArgsConstructor
public class Data {

	private final TenantRepository tenantRepository;
	private final UserRepository userRepository;

	void deleteAll() {
		tenantRepository.deleteAll();
		userRepository.deleteAll();
	}

	public String tenantName() {
		return UUID.randomUUID().toString().substring(0, 20);
	}

	public Tenant tenant() {
		return tenant(tenantName());
	}

	public Tenant tenant(String name) {
		String tenantId = UUID.randomUUID().toString();
		return tenantRepository.save(new Tenant().setEnabled(true)
				.setTenantId(tenantId.substring(0, Math.min(tenantId.length(), 30))).setName(name));
	}

	public User user(String email) {
		return userRepository.save(new User().setUserId(UUID.randomUUID()).setEmail(email));
	}

}
