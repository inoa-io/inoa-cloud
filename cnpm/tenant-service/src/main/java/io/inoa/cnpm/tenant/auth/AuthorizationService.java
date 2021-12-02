package io.inoa.cnpm.tenant.auth;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.slf4j.MDC;

import com.google.rpc.Status;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;

import io.envoyproxy.envoy.config.core.v3.HeaderValue;
import io.envoyproxy.envoy.config.core.v3.HeaderValueOption;
import io.envoyproxy.envoy.service.auth.v3.AuthorizationGrpc;
import io.envoyproxy.envoy.service.auth.v3.CheckRequest;
import io.envoyproxy.envoy.service.auth.v3.CheckResponse;
import io.envoyproxy.envoy.service.auth.v3.OkHttpResponse;
import io.grpc.Status.Code;
import io.grpc.stub.StreamObserver;
import io.inoa.cnpm.tenant.ApplicationProperties;
import io.inoa.cnpm.tenant.domain.Tenant;
import io.inoa.cnpm.tenant.domain.TenantRepository;
import io.inoa.cnpm.tenant.domain.TenantUser;
import io.inoa.cnpm.tenant.domain.TenantUserRepository;
import io.inoa.cnpm.tenant.domain.User;
import io.inoa.cnpm.tenant.domain.UserRepository;
import io.micronaut.core.util.StringUtils;
import io.micronaut.http.HttpHeaderValues;
import io.micronaut.http.HttpHeaders;
import io.micronaut.security.token.jwt.signature.SignatureConfiguration;
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
	private final Set<SignatureConfiguration> upstreamSignatures;
	private final ApplicationProperties properties;
	private final TokenService service;
	private final TenantRepository tenantRepository;
	private final UserRepository userRepository;
	private final TenantUserRepository tenantUserRepository;

	@Override
	public void check(CheckRequest request, StreamObserver<CheckResponse> responseObserver) {
		try {

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

		} finally {
			MDC.remove("tenantId");
			MDC.remove("userId");
			MDC.remove("azp");
			MDC.remove("aud");
		}
	}

	private Optional<SignedJWT> exchangeTokenForRequest(CheckRequest request) {
		var headers = request.getAttributes().getRequest().getHttp().getHeadersMap();

		var token = getToken(headers);
		if (token.isEmpty()) {
			return Optional.empty();
		}

		// got request with local signature

		if (service.isValidLocalToken(token.get())) {
			log.trace("Bybass local signature.");
			return token.map(Token::getJwt);
		}

		// get tenant from request

		var tenant = getTenantFromRequest(headers);
		if (tenant.isEmpty()) {
			return Optional.empty();
		}

		// handle application token

		if (isApplicationToken(token.get())) {
			log.debug("Bybass signature from upstream oidc.");
			return Optional.of(service.exchange(token.get(), tenant.get()));
		}

		// handle user token

		return token.filter(service::isValidRemoteToken)
				.flatMap(this::getUserFromClaims)
				.flatMap(user -> getAssignemnt(tenant.get(), user))
				.map(assignment -> service.exchange(token.get(), assignment.getTenant()));
	}

	private Optional<Tenant> getTenantFromRequest(Map<String, String> headers) {

		var tenantId = Optional
				.ofNullable(headers.get(properties.getTokenExchange().getHttpHeader()))
				.map(String::strip)
				.filter(StringUtils::isNotEmpty);
		if (tenantId.isEmpty()) {
			// probes are without tenantId header, so do not log expected event on info
			log.debug("Ignore request because tenantId header not found.");
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

	private Optional<Token> getToken(Map<String, String> headers) {
		return Optional
				// headers provided by envoy are always lowercase, so adjust header name
				.ofNullable(headers.get(HttpHeaders.AUTHORIZATION.toLowerCase()))
				.filter(value -> value.startsWith(prefix))
				.map(value -> value.substring(prefix.length()))
				.flatMap(service::toToken);
	}

	private boolean isApplicationToken(Token token) {
		return token.getEmailClaim().isEmpty() && upstreamSignatures.stream().anyMatch(signature -> {
			try {
				return signature.verify(token.getJwt());
			} catch (JOSEException e) {
				return false;
			}
		});
	}

	private Optional<User> getUserFromClaims(Token token) {

		var email = token.getEmailClaim();
		if (email.isEmpty()) {
			log.info("Ignore request because email claim not found for token from iss={} with aud={} and azp={}.",
					token.getClaims().getIssuer(), token.getClaims().getAudience(), token.getClaims().getClaim("azp"));
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
