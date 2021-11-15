package io.inoa.tenant;

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

import io.inoa.tenant.domain.Tenant;
import io.inoa.tenant.domain.User;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.hateoas.JsonError;
import io.micronaut.security.token.jwt.signature.SignatureGeneratorConfiguration;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.validation.validator.Validator;
import jakarta.inject.Inject;

/**
 * Abstract test.
 *
 * @author Stephan Schnabel
 */
@MicronautTest(transactional = false, environments = "h2")
@TestMethodOrder(MethodOrderer.DisplayName.class)
public abstract class AbstractTest {

	@Inject
	public Data data;
	@Inject
	Validator validator;
	@Inject
	SignatureGeneratorConfiguration signature;

	@BeforeEach
	void init() {
		data.deleteAll();
	}

	public String auth() {
		return auth(data.user());
	}

	public String auth(Tenant tenant) {
		return auth(data.user(tenant));
	}

	public String auth(User user) {
		return auth(user.getEmail());
	}

	public String auth(String email) {
		return new JwtProvider(signature).builder().subject("meh").claim("email", email).toBearer();
	}

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
			Iterable.class.cast(object).forEach(this::assertValid);
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
