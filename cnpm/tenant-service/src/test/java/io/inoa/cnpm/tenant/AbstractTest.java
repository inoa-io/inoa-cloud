package io.inoa.cnpm.tenant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;

import io.inoa.cnpm.tenant.domain.Data;
import io.inoa.cnpm.tenant.domain.Tenant;
import io.inoa.cnpm.tenant.domain.User;
import io.inoa.cnpm.tenant.messaging.MessagingSink;
import io.inoa.cnpm.tenant.rest.UserRoleVO;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.hateoas.JsonError;
import io.micronaut.security.token.jwt.signature.SignatureGeneratorConfiguration;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.validation.validator.Validator;
import jakarta.inject.Inject;

/**
 * Abstract micronaut test.
 */
@TestMethodOrder(MethodOrderer.DisplayName.class)
@MicronautTest(transactional = false, environments = "h2")
public abstract class AbstractTest {

	@Inject
	public ApplicationProperties properties;
	@Inject
	public Data data;
	@Inject
	public MessagingSink messaging;
	@Inject
	@Client("/")
	public HttpClient rawClient;
	@Inject
	Validator validator;
	@Inject
	SignatureGeneratorConfiguration signature;

	@BeforeEach
	void init() {
		data.deleteAll();
		messaging.deleteAll();
	}

	// auth

	public String auth() {
		return auth(data.user());
	}

	public String auth(Tenant tenant) {
		return auth(tenant, UserRoleVO.ADMIN);
	}

	public String auth(Tenant tenant, UserRoleVO role) {
		return auth(data.assignment(tenant, data.user(), role).getUser());
	}

	public String auth(User user) {
		return auth(user.getEmail());
	}

	public String auth(String email) {
		return new JwtProvider(signature).builder()
				.subject("meh")
				.claim("email", email)
				.toBearer();
	}

	public String authService() {
		return new JwtProvider(signature).builder()
				.subject("meh")
				.claim("aud", List.of("tenant-management", "nope"))
				.toBearer();
	}

	// assertions

	public <T> T assert200(Supplier<HttpResponse<T>> executeable) {
		return assertValid(HttpResponseAssertions.assert200(executeable).body());
	}

	public <T> T assert201(Supplier<HttpResponse<T>> executeable) {
		return assertValid(HttpResponseAssertions.assert201(executeable).body());
	}

	public <T> JsonError assert400(Supplier<HttpResponse<T>> executeable) {
		return assertValid(HttpResponseAssertions.assert400(executeable)).getBody(JsonError.class).get();
	}

	private <T> T assertValid(T object) {
		if (object instanceof Iterable) {
			((Iterable<?>) object).forEach(this::assertValid);
		} else {
			var violations = validator.validate(object);
			assertTrue(violations.isEmpty(), () -> "validation failed with:" + violations.stream()
					.map(v -> "\n\t" + v.getPropertyPath() + ": " + v.getMessage()).collect(Collectors.joining()));
		}
		return object;
	}

	public static <T> void assertSorted(List<T> content, Function<T, String> toKeyFunction, Comparator<T> comparator) {
		var actual = content.stream().map(toKeyFunction).collect(Collectors.toList());
		var expected = content.stream().sorted(comparator).map(toKeyFunction).collect(Collectors.toList());
		assertEquals(expected, actual, "unsorted");
	}
}
