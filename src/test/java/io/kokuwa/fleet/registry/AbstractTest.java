package io.kokuwa.fleet.registry;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;

import io.kokuwa.fleet.registry.auth.AuthTokenService;
import io.kokuwa.fleet.registry.domain.Gateway;
import io.kokuwa.fleet.registry.rest.HttpResponseAssertions;
import io.kokuwa.fleet.registry.rest.JwtProvider;
import io.micronaut.http.HttpHeaderValues;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.hateoas.JsonError;
import io.micronaut.security.token.jwt.signature.SignatureGeneratorConfiguration;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.validation.validator.Validator;

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
	AuthTokenService authTokenService;
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
		properties.getAuth().setExpirationDuration(Duration.ofSeconds(5));
		properties.getAuth().setAudience(UUID.randomUUID().toString());
		properties.getAuth().setIssuer(UUID.randomUUID().toString());
	}

	public String bearer(Gateway gateway) {
		return HttpHeaderValues.AUTHORIZATION_PREFIX_BEARER
				+ " "
				+ authTokenService.createToken(gateway.getExternalId());
	}

	public String bearerAdmin() {
		return new JwtProvider(signature).bearer("admin");
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

	private <T> T assertValid(T object) {
		if (object instanceof Iterable) {
			Iterable.class.cast(object).forEach(this::assertValid);
		} else {
			var violations = validator.validate(object);
			assertTrue(violations.isEmpty(), () -> "validation failed with:" + violations.stream()
					.map(v -> "\n\t" + v.getPropertyPath() + ": " + v.getMessage())
					.collect(Collectors.joining()));
		}
		return object;
	}
}
