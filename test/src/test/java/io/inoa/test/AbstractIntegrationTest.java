package io.inoa.test;

import java.util.UUID;

import io.inoa.test.client.GatewayClientFactory;
import io.inoa.test.client.InfluxDBClient;
import io.inoa.test.client.KeycloakClient;
import io.inoa.test.client.RegistryClient;
import io.micronaut.context.annotation.Value;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;

@MicronautTest(transactional = false)
public abstract class AbstractIntegrationTest extends AbstractTest {

	public static String DEFAULT_TENANT_ID = "iona";

	public @Value("${mqtt.url}") String mqttUrl;
	public @Inject @Client("inoa") HttpClient client;
	public @Inject KeycloakClient keycloak;
	public @Inject InfluxDBClient influxdb;
	public @Inject RegistryClient registry;
	public @Inject GatewayClientFactory gatewayClientFactory;

	public static String gatewayId() {
		return "ISIT01-" + UUID.randomUUID().toString().replace("-", "").toUpperCase().substring(0, 12);
	}
}
