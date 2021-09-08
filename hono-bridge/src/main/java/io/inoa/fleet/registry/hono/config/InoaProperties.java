package io.inoa.fleet.registry.hono.config;

import lombok.Data;

@Data
public class InoaProperties {
	private String gatewayRegistryHost;
	private String keycloakUrl;
	private String clientId;
	private String clientSecret;
	private int gatewayRegistryPort = 80;
}
