package io.inoa.fleet.registry.infrastructure;

import java.util.UUID;

import javax.inject.Inject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;

import io.inoa.fleet.registry.test.GatewayRegistryClient;
import io.inoa.fleet.registry.test.InfluxDBClient;
import io.inoa.fleet.registry.test.MonitoringClient;
import io.micronaut.context.annotation.Value;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;

/**
 * Base for all integration tests.
 *
 * @author Stephan Schnabel
 */
@MicronautTest
@TestMethodOrder(MethodOrderer.DisplayName.class)
public abstract class ComposeTest {

	public static final UUID TENANT_ID = UUID.fromString("2381b39a-e721-4456-8f9f-8d2c18cec993");
	public static final String ADMIN = "admin";
	public static final String USER_TENANT_A = "userFromTenantA";
	public static final String USER_TENANT_B = "userFromTenantB";

	@Inject
	public GatewayRegistryClient registry;
	@Inject
	public InfluxDBClient influxdb;
	@Inject
	public MonitoringClient monitoring;

	@Value("${mqtt.client.server-uri}")
	String mqttUrl;
	boolean mqttUninitialized = true;

	@BeforeEach
	void setMqttUrl() {
		if (mqttUninitialized) {
			registry.withUser(ComposeTest.USER_TENANT_A).setConfiguration("mqtt.url", mqttUrl);
			mqttUninitialized = true;
		}
	}
}
