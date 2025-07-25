package io.inoa.fleet.mqtt.auth;

import java.util.Arrays;

import jakarta.inject.Singleton;

import io.inoa.fleet.mqtt.MqttGatewayIdentifier;
import io.inoa.fleet.mqtt.MqttMetrics;
import io.inoa.fleet.registry.domain.Credential;
import io.inoa.fleet.registry.domain.CredentialRepository;
import io.inoa.fleet.registry.domain.GatewayRepository;
import io.micronaut.cache.CacheManager;
import io.micronaut.cache.SyncCache;
import io.moquette.broker.security.IAuthenticator;
import lombok.extern.slf4j.Slf4j;

@Singleton
@Slf4j
public class InoaAuthenticator implements IAuthenticator {

	private final SyncCache<?> cache;
	private final MqttMetrics mqttMetrics;
	private final CredentialRepository credentialRepository;
	private final GatewayRepository gatewayRepository;

	public InoaAuthenticator(
			MqttMetrics mqttMetrics,
			CacheManager<?> cacheManager,
			CredentialRepository credentialRepository,
			GatewayRepository gatewayRepository) {
		this.mqttMetrics = mqttMetrics;
		this.cache = cacheManager.getCache("authentication");
		this.credentialRepository = credentialRepository;
		this.gatewayRepository = gatewayRepository;
	}

	@Override
	public boolean checkValid(String clientId, String username, byte[] password) {

		// Check identifier
		var gatewayIdentifier = MqttGatewayIdentifier.of(username);
		if (gatewayIdentifier == null) {
			mqttMetrics.counterLoginError("UNKNOWN");
			return false;
		}

		// Check if gateway exist
		var gateway = gatewayRepository.findByGatewayId(gatewayIdentifier.gatewayId());
		if (gateway.isEmpty()) {
			mqttMetrics.counterLoginError(gatewayIdentifier.tenantId());
			log.debug("Gateway not found: {}@{}",
					gatewayIdentifier.gatewayId(), gatewayIdentifier.tenantId());
			return false;
		}

		// Check if credentials exist
		var credential = cache.get(gatewayIdentifier.gatewayId(), Credential.class).orElse(null);
		if (credential == null) {
			var foundCredential = credentialRepository.findByGatewayAndName(gateway.get(), "initial");
			if (foundCredential.isEmpty()) {
				mqttMetrics.counterLoginError(gatewayIdentifier.tenantId());
				log.debug("No credentials for gateway: {}@{}",
						gatewayIdentifier.gatewayId(), gatewayIdentifier.tenantId());
				return false;
			}
			credential = foundCredential.get();
			cache.put(gatewayIdentifier.gatewayId(), credential);
		}

		// Check if credentials are correct
		if (!Arrays.equals(credential.getValue(), password)) {
			mqttMetrics.counterLoginError(gatewayIdentifier.tenantId());
			log.warn("Login attempt with wrong credentials: {}@{}",
					gatewayIdentifier.gatewayId(), gatewayIdentifier.tenantId());
			return false;
		}

		// Check if gateway is enabled
		if (!gateway.get().getEnabled()) {
			log.debug("Gateway not enabled: {}@{}",
					gatewayIdentifier.gatewayId(), gatewayIdentifier.tenantId());
			return false;
		}

		return true;
	}
}
