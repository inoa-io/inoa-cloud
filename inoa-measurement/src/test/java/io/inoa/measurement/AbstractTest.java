package io.inoa.measurement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import io.inoa.measurement.api.HttpResponseAssertions;
import io.inoa.measurement.api.JwtProvider;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.hateoas.JsonError;
import io.micronaut.security.token.jwt.signature.SignatureGeneratorConfiguration;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.test.support.TestPropertyProvider;
import io.micronaut.validation.validator.Validator;
import jakarta.inject.Inject;

@TestMethodOrder(MethodOrderer.DisplayName.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@MicronautTest
public abstract class AbstractTest implements TestPropertyProvider {

	private static Map<String, String> properties;

	@Inject
	public Data data;

	@Inject
	Validator validator;

	@Inject
	SignatureGeneratorConfiguration signature;

	public static <T> void assertSorted(List<T> content, Function<T, String> toKeyFunction, Comparator<T> comparator) {
		var actual = content.stream().map(toKeyFunction).toList();
		var expected = content.stream().sorted(comparator).map(toKeyFunction).toList();
		assertEquals(expected, actual, "unsorted");
	}

	@BeforeEach
	void init() {

		// delete existing data
		data.deleteAll();
	}

	public String auth() {
		return new JwtProvider(signature).builder().claim("email", "test@test.de").subject("admin").toBearer();
	}

	public String auth(String tenant) {
		return new JwtProvider(signature).builder().claim("email", "test@test.de").claim("tenant", tenant)
			.subject("admin").toBearer();
	}

	public <T> T assert200(Supplier<HttpResponse<T>> executeable) {
		return assertValid(HttpResponseAssertions.assert200(executeable).body());
	}

	public <T> T assert201(Supplier<HttpResponse<T>> executeable) {
		return assertValid(HttpResponseAssertions.assert201(executeable).body());
	}

	public <T> void assert204(Supplier<HttpResponse<T>> executeable) {
		assertValid(HttpResponseAssertions.assert204(executeable));
	}

	public <T> JsonError assert400(Supplier<HttpResponse<T>> executeable) {
		return assertValid(HttpResponseAssertions.assert400(executeable)).getBody(JsonError.class).get();
	}

	public <T> void assert401(Supplier<HttpResponse<T>> executeable) {
		assertValid(HttpResponseAssertions.assert401(executeable));
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
				.map(v -> "\n\t" + v.getPropertyPath() + ": " + v.getMessage()).collect(Collectors.joining()));
		}
		return object;
	}

	@Override
	@SuppressWarnings("resource")
	public Map<String, String> getProperties() {
		if (properties == null) {

			var organisation = "test-org";
			var bucket = "test-bucket";
			var token = "changeMe";

			var influxContainer = new GenericContainer<>(DockerImageName.parse("influxdb:2.1.1-alpine"))
				.withEnv("DOCKER_INFLUXDB_INIT_MODE", "setup")
				.withEnv("DOCKER_INFLUXDB_INIT_USERNAME", "username")
				.withEnv("DOCKER_INFLUXDB_INIT_PASSWORD", "password")
				.withEnv("DOCKER_INFLUXDB_INIT_ORG", organisation)
				.withEnv("DOCKER_INFLUXDB_INIT_BUCKET", bucket)
				.withEnv("DOCKER_INFLUXDB_INIT_ADMIN_TOKEN", token)
				.withExposedPorts(8086)
				.waitingFor(Wait.forListeningPort());
			influxContainer.start();

			properties = Map.of(
				"influxdb.url", "http://" + influxContainer.getHost() + ":" + influxContainer.getMappedPort(8086),
				"influxdb.token", token,
				"influxdb.organisation", organisation,
				"influxdb.bucket", bucket);
		}
		return properties;
	}
}

