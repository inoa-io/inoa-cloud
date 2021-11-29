package io.inoa.tenant.auth;

import java.util.Optional;

import org.slf4j.MDC;

import com.google.rpc.Status;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import io.envoyproxy.envoy.config.core.v3.HeaderValue;
import io.envoyproxy.envoy.config.core.v3.HeaderValueOption;
import io.envoyproxy.envoy.service.auth.v3.AttributeContext.Request;
import io.envoyproxy.envoy.service.auth.v3.AuthorizationGrpc;
import io.envoyproxy.envoy.service.auth.v3.CheckRequest;
import io.envoyproxy.envoy.service.auth.v3.CheckResponse;
import io.envoyproxy.envoy.service.auth.v3.OkHttpResponse;
import io.grpc.Status.Code;
import io.grpc.stub.StreamObserver;
import io.inoa.tenant.ApplicationProperties;
import io.inoa.tenant.domain.Tenant;
import io.inoa.tenant.domain.TenantRepository;
import io.inoa.tenant.domain.TenantUser;
import io.inoa.tenant.domain.TenantUserRepository;
import io.inoa.tenant.domain.User;
import io.inoa.tenant.domain.UserRepository;
import io.micronaut.core.util.StringUtils;
import io.micronaut.http.HttpHeaderValues;
import io.micronaut.http.HttpHeaders;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Authorization service for token exchange.
 *
 * @author Rico Pahlisch
 * @author Stephan Schnabel
 */
@Singleton
@Slf4j
@RequiredArgsConstructor
public class AuthorizationService extends AuthorizationGrpc.AuthorizationImplBase {

	private final String prefix = HttpHeaderValues.AUTHORIZATION_PREFIX_BEARER + " ";
	private final ApplicationProperties properties;
	private final TokenService service;
	private final TenantRepository tenantRepository;
	private final UserRepository userRepository;
	private final TenantUserRepository tenantUserRepository;

	@Override
	public void check(CheckRequest request, StreamObserver<CheckResponse> responseObserver) {

		var checkResponse = exchangeTokenForRequest(request)
				.map(jwt -> HeaderValue.newBuilder()
						.setKey(HttpHeaders.AUTHORIZATION)
						.setValue(HttpHeaderValues.AUTHORIZATION_PREFIX_BEARER + " " + jwt.serialize())
						.build())
				.map(header -> CheckResponse.newBuilder()
						.setStatus(Status.newBuilder().setCode(Code.OK.value()).build())
						.setOkResponse(OkHttpResponse.newBuilder()
								.addHeaders(HeaderValueOption.newBuilder().setHeader(header).build())
								.build())
						.build())
				.orElseGet(() -> CheckResponse.newBuilder()
						.setStatus(Status.newBuilder().setCode(Code.UNAUTHENTICATED.value()).build())
						.build());

		responseObserver.onNext(checkResponse);
		responseObserver.onCompleted();
	}

	private Optional<SignedJWT> exchangeTokenForRequest(CheckRequest request) {

		var tenant = getTenantFromRequest(request.getAttributes().getRequest());
		if (tenant.isEmpty()) {
			return Optional.empty();
		}

		var claims = getClaimsFromRequest(request.getAttributes().getRequest());
		if (claims.isEmpty()) {
			return Optional.empty();
		}

		var user = getUserFromClaims(claims.get());
		if (user.isEmpty()) {
			return Optional.empty();
		}

		var assignemnt = getAssignemnt(tenant.get(), user.get());
		if (assignemnt.isEmpty()) {
			return Optional.empty();
		}

		return Optional.of(service.exchange(claims.get(), assignemnt.get()));
	}

	private Optional<Tenant> getTenantFromRequest(Request request) {

		var tenantId = Optional
				.ofNullable(request.getHttp().getHeadersMap().get(properties.getHttpHeader()))
				.map(String::strip)
				.filter(StringUtils::isNotEmpty);
		if (tenantId.isEmpty()) {
			log.info("Ignore request because tenantId header not found.");
			return Optional.empty();
		}
		MDC.put("tenantId", tenantId.get());

		var tenant = tenantId.flatMap(tenantRepository::findByTenantId);
		if (tenant.isEmpty()) {
			log.info("Ignore request because tenantId is unknown.");
			return Optional.empty();
		}
		if (!tenant.get().getEnabled()) {
			log.info("Ignore request because tenant {} is disabled.", tenantId.get());
			return Optional.empty();
		}

		return tenant;
	}

	private Optional<JWTClaimsSet> getClaimsFromRequest(Request request) {
		return Optional
				.ofNullable(request.getHttp().getHeadersMap().get(HttpHeaders.AUTHORIZATION))
				.filter(value -> value.startsWith(prefix))
				.map(value -> value.substring(prefix.length()))
				.flatMap(service::parse);
	}

	private Optional<User> getUserFromClaims(JWTClaimsSet claims) {

		var email = Optional
				.ofNullable(claims.getClaim("email"))
				.filter(String.class::isInstance)
				.map(String.class::cast);
		if (email.isEmpty()) {
			log.info("Ignore request because email claim not found.");
			return Optional.empty();
		}

		var user = email.flatMap(userRepository::findByEmail);
		if (user.isEmpty()) {
			log.info("Ignore request because user email is unknown.");
		} else {
			MDC.put("userId", user.get().getUserId().toString());
		}

		return user;
	}

	private Optional<TenantUser> getAssignemnt(Tenant tenant, User user) {

		var assignment = tenantUserRepository.findByTenantAndUser(tenant, user);
		if (assignment.isEmpty()) {
			log.info("Ignore request because tenant {} is not assigned to user {}.",
					tenant.getTenantId(), user.getUserId());
			return Optional.empty();
		}

		return assignment;
	}
}
