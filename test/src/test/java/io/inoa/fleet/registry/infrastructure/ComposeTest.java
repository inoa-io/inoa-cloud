package io.inoa.fleet.registry.infrastructure;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.validation.Validator;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;

import com.influxdb.client.InfluxDBClient;

import io.inoa.fleet.registry.rest.HttpResponseAssertions;
import io.inoa.fleet.registry.rest.gateway.AuthApiClient;
import io.inoa.fleet.registry.rest.gateway.PropertiesApiClient;
import io.inoa.fleet.registry.rest.management.CredentialsApiClient;
import io.inoa.fleet.registry.rest.management.GatewaysApiClient;
import io.inoa.fleet.registry.rest.management.TenantsApiClient;
import io.inoa.fleet.registry.test.InoaExporterPrometheusClient;
import io.inoa.fleet.registry.test.InoaTranslatorPrometheusClient;
import io.inoa.fleet.registry.test.KafkaBackupPrometheusClient;
import io.micronaut.context.annotation.Value;
import io.micronaut.http.HttpResponse;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;

/**
 * Base for all integration tests.
 *
 * @author Stephan Schnabel
 */
@MicronautTest
@TestMethodOrder(MethodOrderer.DisplayName.class)
public abstract class ComposeTest {

	@Inject
	Validator validator;
	@Inject
	public Auth auth;

	@Inject
	public GatewaysApiClient gatewaysClient;
	@Inject
	public CredentialsApiClient credentialsClient;
	@Inject
	public AuthApiClient authClient;
	@Inject
	public PropertiesApiClient propertiesClient;
	@Inject
	public TenantsApiClient tenantsApiClient;

	@Inject
	public KafkaBackupPrometheusClient kafkaBackupPrometheusClient;
	@Inject
	public InoaTranslatorPrometheusClient inoaTranslatorPrometheusClient;
	@Inject
	public InoaExporterPrometheusClient inoaExporterPrometheusClient;

	@Value("${mqtt.client.server-uri}")
	public String mqttServerUrl;
	@Inject
	public InfluxDBClient influxdb;

	public <T> T assert200(Supplier<HttpResponse<T>> executeable) {
		return assertValid(HttpResponseAssertions.assert200(executeable).body());
	}

	public <T> T assert201(Supplier<HttpResponse<T>> executeable) {
		return assertValid(HttpResponseAssertions.assert201(executeable).body());
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
