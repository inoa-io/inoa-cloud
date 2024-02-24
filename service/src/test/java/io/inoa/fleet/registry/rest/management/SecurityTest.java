package io.inoa.fleet.registry.rest.management;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.inoa.test.AbstractUnitTest;
import io.micronaut.http.HttpAttributes;
import io.micronaut.http.HttpMethod;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.context.ServerRequestContext;
import io.micronaut.http.exceptions.HttpStatusException;
import io.micronaut.http.simple.SimpleHttpRequest;
import io.micronaut.security.authentication.ServerAuthentication;
import io.micronaut.security.token.Claims;
import jakarta.inject.Inject;

/**
 * Tests for {@link Security}.
 *
 * @author Stephan Schnabel
 */
@DisplayName("management: security")
public class SecurityTest extends AbstractUnitTest {

	@Inject Security security;

	@DisplayName("getTenantId(): success with tenant id from authentication")
	@Test
	void getTenantIdSuccessAuthentication() {
		ServerRequestContext.with(request("abc", null, null), (Runnable) () -> {
			assertEquals("abc", security.getTenantIds().iterator().next(), "tenantId");
		});
	}

	@DisplayName("getTenantId(): success with tenant id from audience string")
	@Test
	void getTenantIdSuccessAusdienceSingle() {
		ServerRequestContext.with(request(null, "test", "abc"), (Runnable) () -> {
			assertEquals("abc", security.getTenantIds().iterator().next(), "tenantId");
		});
	}

	@DisplayName("getTenantId(): success with tenant id from audience list")
	@Test
	void getTenantIdSuccessAusdienceList() {
		ServerRequestContext.with(request(null, List.of("nope", "test"), "abc"), (Runnable) () -> {
			assertEquals("abc", security.getTenantIds().iterator().next(), "tenantId");
		});
	}

	@DisplayName("getTenantId(): fail without tenant claim")
	@Test
	void getTenantIdFailNoClaim() {
		ServerRequestContext.with(request(null, null, null), (Runnable) () -> {
			try {
				security.getTenantIds();
				fail("without claim http status exception expected");
			} catch (HttpStatusException e) {
				assertEquals(HttpStatus.UNAUTHORIZED, e.getStatus(), "status");
			}
		});
	}

	@DisplayName("getTenantId(): fail without tenant header but present audience")
	@Test
	void getTenantIdFailNoTenantHeader() {
		ServerRequestContext.with(request(null, "test", null), (Runnable) () -> {
			try {
				security.getTenantIds();
				fail("without claim http status exception expected");
			} catch (HttpStatusException e) {
				assertEquals(HttpStatus.UNAUTHORIZED, e.getStatus(), "status");
			}
		});
	}

	@DisplayName("getTenantId(): fail without request")
	@Test
	void getTenantIdFailNoRequest() {
		assertTrue(ServerRequestContext.currentRequest().isEmpty(), "request");
		try {
			security.getTenantIds();
			fail("without request http status exception expected");
		} catch (HttpStatusException e) {
			assertEquals(HttpStatus.UNAUTHORIZED, e.getStatus(), "status");
		}
	}

	@DisplayName("getTenant(): success")
	@Test
	void getTenantSuccess() {
		var tenantId = data.tenant().getTenantId();
		ServerRequestContext.with(request(tenantId, null, null), (Runnable) () -> {
			assertEquals(tenantId, security.getGrantedTenants().iterator().next().getTenantId(), "tenantId");
		});
	}

	@DisplayName("getTenant(): fail with unknown tenant")
	@Test
	void getTenantFailUnknown() {
		ServerRequestContext.with(request("asdf", null, null), (Runnable) () -> {
			try {
				security.getGrantedTenants();
				fail("without tenant http status exception expected");
			} catch (HttpStatusException e) {
				assertEquals(HttpStatus.UNAUTHORIZED, e.getStatus(), "status");
			}
		});
	}

	@DisplayName("getTenant(): fail with deleted tenant")
	@Test
	void getTenantFailDeleted() {
		var tenantId = data.tenant("inoa", true, true).getTenantId();
		ServerRequestContext.with(request(tenantId, null, null), (Runnable) () -> {
			try {
				security.getGrantedTenants();
				fail("without tenant http status exception expected");
			} catch (HttpStatusException e) {
				assertEquals(HttpStatus.UNAUTHORIZED, e.getStatus(), "status");
			}
		});
	}

	private HttpRequest<Object> request(Object tenantClaim, Object audience, String tenantHeader) {

		var headers = new HashMap<String, Object>();
		if (tenantClaim != null) {
			headers.put(properties.getSecurity().getClaimTenants(), tenantClaim);
		}
		if (audience != null) {
			headers.put(Claims.AUDIENCE, audience);
		}

		var request = new SimpleHttpRequest<>(HttpMethod.GET, "/", null);
		request.getHeaders().add(properties.getSecurity().getTenantHeaderName(), tenantHeader);
		request.getAttributes().put(HttpAttributes.PRINCIPAL.toString(), new ServerAuthentication("", null, headers));
		return request;
	}
}
