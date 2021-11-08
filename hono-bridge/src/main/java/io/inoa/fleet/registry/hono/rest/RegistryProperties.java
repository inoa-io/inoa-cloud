package io.inoa.fleet.registry.hono.rest;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@ConfigurationProperties("hono.inoa")
@Getter
@Setter
public class RegistryProperties {

	private String gatewayRegistryHost;
	private int gatewayRegistryPort = 80;
	private String keycloakUrl;
	private String clientId;
	private String clientSecret;
}
