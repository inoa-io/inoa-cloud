package io.inoa.fleet.registry.hono.rest;

import java.time.Duration;

import org.eclipse.hono.util.CacheDirective;
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
	private Duration tenantCacheDuration;
	private Duration gatewayCacheDuration;
	private Duration credentialsCacheDuration;

	public CacheDirective getTenantCache() {
		return tenantCacheDuration == null
				? CacheDirective.noCacheDirective()
				: CacheDirective.maxAgeDirective(tenantCacheDuration);
	}

	public CacheDirective getGatewayCache() {
		return gatewayCacheDuration == null
				? CacheDirective.noCacheDirective()
				: CacheDirective.maxAgeDirective(gatewayCacheDuration);
	}

	public CacheDirective getCredentialsCache() {
		return credentialsCacheDuration == null
				? CacheDirective.noCacheDirective()
				: CacheDirective.maxAgeDirective(credentialsCacheDuration);
	}
}
