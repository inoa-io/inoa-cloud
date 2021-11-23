package io.inoa.fleet.registry.test;

import static io.inoa.fleet.registry.rest.HttpResponseAssertions.assert200;
import static io.inoa.fleet.registry.rest.HttpResponseAssertions.assert204;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.awaitility.Awaitility;

import com.nimbusds.jose.jwk.JWKSet;

import io.inoa.fleet.registry.infrastructure.TestAssertions;
import io.inoa.fleet.registry.rest.gateway.PropertiesApiClient;
import io.inoa.fleet.registry.rest.management.ConfigurationApiClient;
import io.inoa.fleet.registry.rest.management.ConfigurationSetVO;
import io.inoa.fleet.registry.rest.management.CredentialCreateVO;
import io.inoa.fleet.registry.rest.management.CredentialTypeVO;
import io.inoa.fleet.registry.rest.management.CredentialsApiClient;
import io.inoa.fleet.registry.rest.management.GatewayCreateVO;
import io.inoa.fleet.registry.rest.management.GatewayDetailVO;
import io.inoa.fleet.registry.rest.management.GatewaysApiClient;
import io.inoa.fleet.registry.rest.management.SecretCreatePSKVO;
import io.inoa.fleet.registry.rest.management.SecretCreatePasswordVO;
import io.inoa.fleet.registry.rest.management.TenantVO;
import io.inoa.fleet.registry.rest.management.TenantsApiClient;
import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@Singleton
@RequiredArgsConstructor
public class GatewayRegistryClient {

	private final KeycloakClient keycloak;
	private final TestAssertions assertions;
	private final GatewayRegistryFilter filter;
	private final TenantsApiClient tenantsClient;
	private final GatewaysApiClient gatewaysClient;
	private final CredentialsApiClient credentialsClient;
	private final ConfigurationApiClient configurationClient;
	private final AuthApiFixedClient authClient;
	private final PropertiesApiClient propertiesClient;
	private final io.inoa.fleet.registry.rest.gateway.ConfigurationApiClient configurationGatewayClient;

	@Value("${mqtt.client.server-uri}")
	String mqttUri;

	// tenant

	public GatewayRegistryClient withTenantId(String tenantId) {
		filter.setTenantId(tenantId);
		return this;
	}

	/** Waits until tenant is created and updates mqtt configuration for test setup. */
	public GatewayRegistryClient waitForTenant(String tenantId) {
		withTenantId(tenantId);
		Awaitility.await("tenant created in registry").until(() -> findTenant(tenantId).isPresent());
		assert204(() -> configurationClient.setConfiguration(keycloak.auth(), "mqtt.uri",
				new ConfigurationSetVO().setValue(mqttUri)));
		return this;
	}

	public Optional<TenantVO> findTenant(String tenantId) {
		return tenantsClient.findTenant(keycloak.auth(), tenantId).getBody();
	}

	// gateway

	@SneakyThrows
	public JWKSet getJwkSet() {
		return JWKSet.parse(assert200(() -> authClient.getKeys()).getBody(Map.class).orElseThrow());
	}

	// management

	public GatewayClient createGateway() {
		return createGateway("dev-" + UUID.randomUUID().toString().substring(0, 10), UUID.randomUUID().toString());
	}

	public GatewayClient createGateway(String name, String secret) {

		var vo = new GatewayCreateVO().setName(name);
		var gateway = assertions.assert201(() -> gatewaysClient.createGateway(keycloak.auth(), vo));
		var gatewayId = gateway.getGatewayId();
		assertions.assert201(() -> credentialsClient.createCredential(keycloak.auth(), gatewayId,
				new CredentialCreateVO()
						.setAuthId("registry")
						.setType(CredentialTypeVO.PSK)
						.setSecrets(List.of(new SecretCreatePSKVO().setSecret(secret.getBytes())))));
		assertions.assert201(() -> credentialsClient.createCredential(keycloak.auth(), gatewayId,
				new CredentialCreateVO()
						.setAuthId("hono")
						.setType(CredentialTypeVO.PASSWORD)
						.setSecrets(List.of(new SecretCreatePasswordVO().setPassword(secret.getBytes())))));

		return toClient(filter.getTenantId(), gatewayId, secret);
	}

	public GatewayClient toClient(String tenantId, UUID gatewayId, String secret) {
		return new GatewayClient(authClient, propertiesClient, configurationGatewayClient, tenantId, gatewayId, secret);
	}

	public Optional<GatewayClient> toClient(String name, String secret) {
		return assertions
				.assert200(() -> gatewaysClient.findGateways(keycloak.auth(), null, Optional.of(100), null, null))
				.getContent().stream()
				.filter(gateway -> gateway.getName().equals(name)).findAny()
				.map(gateway -> toClient(filter.getTenantId(), gateway.getGatewayId(), secret));
	}

	public GatewayDetailVO findGateway(GatewayClient gateway) {
		return findGateway(gateway.getGatewayId());
	}

	public GatewayDetailVO findGateway(UUID gatewayId) {
		return assertions.assert200(() -> gatewaysClient.findGateway(keycloak.auth(), gatewayId));
	}
}
