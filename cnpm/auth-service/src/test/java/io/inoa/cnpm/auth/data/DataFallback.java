package io.inoa.cnpm.auth.data;

import java.util.List;
import java.util.UUID;

import io.inoa.cnpm.tenant.management.TenantCreateVO;
import io.inoa.cnpm.tenant.management.TenantPageEntryVO;
import io.inoa.cnpm.tenant.management.TenantUpdateVO;
import io.inoa.cnpm.tenant.management.TenantVO;
import io.inoa.cnpm.tenant.management.TenantsApi;
import io.inoa.cnpm.tenant.management.UserCreateVO;
import io.inoa.cnpm.tenant.management.UserPageVO;
import io.inoa.cnpm.tenant.management.UserUpdateVO;
import io.inoa.cnpm.tenant.management.UserVO;
import io.inoa.cnpm.tenant.management.UsersApi;
import io.micronaut.http.HttpResponse;
import io.micronaut.retry.annotation.Fallback;
import lombok.RequiredArgsConstructor;

/**
 * HTTP client fallback for {@link TenantsApi} und {@link UsersApi};
 */
@Fallback
@RequiredArgsConstructor
public class DataFallback implements TenantsApi, UsersApi {

	private final Data data;

	@Override
	public HttpResponse<TenantVO> findTenant(String tenantId) {
		return data.findTenant(tenantId).map(HttpResponse::ok).orElseGet(HttpResponse::notFound);
	}

	@Override
	public HttpResponse<UserPageVO> findUsers(
			String tenantId, Integer page, Integer size, List<String> sort, String filter) {
		var users = data.findUser(tenantId, filter).map(List::of).orElseGet(List::of);
		return HttpResponse.ok(new UserPageVO().setContent(users).setTotalSize(users.size()));
	}

	@Override
	public HttpResponse<UserVO> findUser(String tenantId, UUID userId) {
		throw new UnsupportedOperationException();
	}

	@Override
	public HttpResponse<List<TenantPageEntryVO>> findTenants() {
		throw new UnsupportedOperationException();
	}

	@Override
	public HttpResponse<TenantVO> createTenant(String tenantId, TenantCreateVO vo) {
		throw new UnsupportedOperationException();
	}

	@Override
	public HttpResponse<TenantVO> updateTenant(String tenantId, TenantUpdateVO vo) {
		throw new UnsupportedOperationException();
	}

	@Override
	public HttpResponse<Object> deleteTenant(String tenantId) {
		throw new UnsupportedOperationException();
	}

	@Override
	public HttpResponse<UserVO> createUser(String tenantId, UserCreateVO vo) {
		throw new UnsupportedOperationException();
	}

	@Override
	public HttpResponse<UserVO> updateUser(String tenantId, UUID userId, UserUpdateVO vo) {
		throw new UnsupportedOperationException();
	}

	@Override
	public HttpResponse<Object> deleteUser(String tenantId, UUID userId) {
		throw new UnsupportedOperationException();
	}
}
