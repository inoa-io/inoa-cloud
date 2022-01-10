package io.inoa.cnpm.auth.service;

import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.UnknownHostException;
import java.util.Optional;
import java.util.function.Supplier;

import io.inoa.cnpm.tenant.management.TenantVO;
import io.inoa.cnpm.tenant.management.TenantsApiClient;
import io.inoa.cnpm.tenant.management.UserVO;
import io.inoa.cnpm.tenant.management.UsersApiClient;
import io.micronaut.cache.annotation.Cacheable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.client.exceptions.HttpClientException;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.http.client.exceptions.ReadTimeoutException;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Singleton
@RequiredArgsConstructor
@Slf4j
public class DataClient {

	private final TenantsApiClient tenants;
	private final UsersApiClient users;

	@Cacheable("tenant")
	public Optional<TenantVO> findTenant(String tenantId) {
		return execute(() -> tenants.findTenant(tenantId));
	}

	@Cacheable("user")
	public Optional<UserVO> findUser(String tenantId, String email) {
		return execute(() -> users.findUsers(tenantId, 0, 1, null, email))
				.flatMap(page -> page.getContent().stream().findFirst());
	}

	static <T> Optional<T> execute(Supplier<HttpResponse<T>> supplier) {
		try {
			return supplier.get().getBody();
		} catch (HttpClientResponseException e) {
			log.warn("Service returned status {}: {}", e.getStatus().getCode(),
					e.getResponse().getBody(String.class).orElse(null));
		} catch (ReadTimeoutException e) {
			log.warn("Service read timeout occurred.");
		} catch (HttpClientException e) {
			if (e.getCause() instanceof ConnectException) {
				log.warn("Service not available: {}", e.getMessage());
			} else if (e.getCause() instanceof NoRouteToHostException) {
				log.warn("Service not available: {}", e.getMessage());
			} else if (e.getCause() instanceof UnknownHostException) {
				log.warn("Service not available: {}", e.getMessage());
			} else {
				log.error("Service call failed.", e);
			}
		}
		return Optional.empty();
	}
}
