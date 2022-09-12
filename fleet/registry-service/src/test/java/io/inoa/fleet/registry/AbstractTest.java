package io.inoa.fleet.registry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;

import io.inoa.fleet.registry.domain.Tenant;
import io.inoa.fleet.registry.rest.HttpResponseAssertions;
import io.inoa.fleet.registry.rest.JwtProvider;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.hateoas.JsonError;
import io.micronaut.security.token.jwt.signature.SignatureGeneratorConfiguration;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.validation.validator.Validator;
import jakarta.inject.Inject;

/**
 * Base for all unit tests.
 *
 * @author Stephan Schnabel
 */
@MicronautTest(transactional = false, environments = "h2")
@TestMethodOrder(MethodOrderer.DisplayName.class)
public abstract class AbstractTest {

	@Inject
	public Data data;
	@Inject
	public ApplicationProperties properties;
	@Inject
	Validator validator;
	@Inject
	SignatureGeneratorConfiguration signature;

	@BeforeEach
	void init() {

		// delete existing data

		data.deleteAll();

		// set random properties

		properties.getGateway().getToken().setForceNotBefore(true);
		properties.getGateway().getToken().setForceJwtId(true);
		properties.getGateway().getToken().setForceIssuedAt(true);
		properties.getGateway().getToken().setIssuedAtThreshold(Optional.of(Duration.ofSeconds(5)));
	}

	public String auth(Tenant tenant) {
		return auth(tenant.getTenantId());
	}

	public String auth(String tenantId) {
		return new JwtProvider(signature).builder()
				.subject("admin")
				.claim(properties.getSecurity().getClaimTenant(), tenantId)
				.toBearer();
	}

	// asserts

	public <T> T assert200(Supplier<HttpResponse<T>> executeable) {
		return assertValid(HttpResponseAssertions.assert200(executeable).body());
	}

	public <T> T assert201(Supplier<HttpResponse<T>> executeable) {
		return assertValid(HttpResponseAssertions.assert201(executeable).body());
	}

	public <T> JsonError assert400(Supplier<HttpResponse<T>> executeable) {
		return assertValid(HttpResponseAssertions.assert400(executeable)).getBody(JsonError.class).get();
	}

	public <T> void assert404(String message, Supplier<HttpResponse<T>> executeable) {
		var error = assertValid(HttpResponseAssertions.assert404(executeable)).getBody(JsonError.class).get();
		assertEquals(message, error.getMessage(), "json error message");
	}

	private <T> T assertValid(T object) {
		if (object instanceof Iterable<?> iterable) {
			iterable.forEach(this::assertValid);
		} else {
			var violations = validator.validate(object);
			assertTrue(violations.isEmpty(), () -> "validation failed with:" + violations.stream()
					.map(v -> "\n\t" + v.getPropertyPath() + ": " + v.getMessage())
					.collect(Collectors.joining()));
		}
		return object;
	}

	public static <T> void assertSorted(List<T> content, Function<T, String> toKeyFunction, Comparator<T> comparator) {
		var actual = content.stream().map(toKeyFunction).toList();
		var expected = content.stream().sorted(comparator).map(toKeyFunction).toList();
		assertEquals(expected, actual, "unsorted");
	}
}
