package io.inoa.fleet.auth;

import java.util.UUID;

import javax.inject.Singleton;
import javax.transaction.Transactional;

import io.inoa.fleet.auth.domain.Tenant;
import io.inoa.fleet.auth.domain.TenantRepository;
import lombok.RequiredArgsConstructor;

@Singleton
@Transactional(Transactional.TxType.REQUIRES_NEW)
@RequiredArgsConstructor
public class Data {

	private final TenantRepository tenantRepository;

	void deleteAll() {
		tenantRepository.deleteAll();
	}

	public String tenantName() {
		return UUID.randomUUID().toString().substring(0, 20);
	}

	public Tenant tenant() {
		return tenant(tenantName());
	}

	public Tenant tenant(String name) {
		return tenantRepository.save(new Tenant().setTenantId(UUID.randomUUID()).setName(name));
	}

}
