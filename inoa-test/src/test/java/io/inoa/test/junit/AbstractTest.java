package io.inoa.test.junit;

import java.util.UUID;

import org.junit.jupiter.api.MethodOrderer.DisplayName;
import org.junit.jupiter.api.TestMethodOrder;

import io.inoa.test.client.GatewayClientFactory;
import io.inoa.test.client.InfluxDBClient;
import io.inoa.test.client.KeycloakClient;
import io.inoa.test.client.RegistryClient;
import io.micronaut.context.annotation.Value;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;

@MicronautTest
@TestMethodOrder(DisplayName.class)
public abstract class AbstractTest {

	public static String DEFAULT_TENANT_ID = "iona";

	public @Value("${mqtt.url}") String mqttUrl;
	@Inject
	public KeycloakClient keycloak;
	@Inject
	public InfluxDBClient influxdb;
	@Inject
	public RegistryClient registry;
	@Inject
	public GatewayClientFactory gatewayClientFactory;

	public static String gatewayId() {
		return "ISIT01-" + UUID.randomUUID().toString().replace("-", "").toUpperCase().substring(0, 12);
	}
}
