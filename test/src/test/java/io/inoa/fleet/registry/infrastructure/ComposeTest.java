package io.inoa.fleet.registry.infrastructure;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;

import io.inoa.fleet.registry.test.GatewayRegistryClient;
import io.inoa.fleet.registry.test.InfluxDBClient;
import io.inoa.fleet.registry.test.MonitoringClient;
import io.inoa.fleet.registry.test.TenantServiceClient;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;

/**
 * Base for all integration tests.
 *
 * @author Stephan Schnabel
 */
@MicronautTest
@TestMethodOrder(MethodOrderer.DisplayName.class)
public abstract class ComposeTest {

	@Inject
	public TenantServiceClient tenantService;
	@Inject
	public GatewayRegistryClient gatewayRegistry;
	@Inject
	public InfluxDBClient influxdb;
	@Inject
	public MonitoringClient monitoring;
}
