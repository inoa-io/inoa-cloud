package io.inoa.junit;

import java.util.UUID;

import org.junit.jupiter.api.MethodOrderer.DisplayName;
import org.junit.jupiter.api.TestMethodOrder;

import io.inoa.client.GatewayClientFactory;
import io.inoa.client.InfluxDBClient;
import io.inoa.client.KeycloakClient;
import io.inoa.client.RegistryClient;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;

@MicronautTest
@TestMethodOrder(DisplayName.class)
public abstract class AbstractTest {

	public static String TENANT_INOA = "iona";

	@Inject
	public KeycloakClient keycloak;
	@Inject
	public InfluxDBClient influxdb;
	@Inject
	public RegistryClient registry;
	@Inject
	public GatewayClientFactory gatewayClientFactory;

	public static String gatewayId() {
		return "ISIT01-" + UUID.randomUUID().toString().replaceAll("-", "").toUpperCase().substring(0, 10);
	}
}
