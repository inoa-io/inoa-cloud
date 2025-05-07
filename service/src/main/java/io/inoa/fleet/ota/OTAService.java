package io.inoa.fleet.ota;

import java.util.Collections;

import jakarta.inject.Singleton;

import org.eclipse.hawkbit.api.TargetsApiClient;
import org.eclipse.hawkbit.model.TargetsCreationRequestPartVO;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/** Service for handling OTA related stuff. Decouples Hawkbit API from INOA Fleet. */
@Slf4j
@Singleton
@AllArgsConstructor
public class OTAService {

	private final TargetsApiClient targetsApiClient;

	/**
	 * Check if gateway is registered in Hawkbit
	 *
	 * @param gatewayId the gateway ID
	 * @return true if gateway is known to Hawkbit
	 */
	public boolean checkGatewayRegistered(String gatewayId) {
		var response = targetsApiClient.getTarget(gatewayId);
		return switch (response.status()) {
			case OK -> true;
			case NOT_FOUND -> false;
			default -> {
				log.error(
						"Unable to check if gateway is known to Hawkbit. Got status code {}: {}",
						response.code(),
						response.body());
				throw new IllegalStateException("Unable to check if gateway is known to Hawkbit.");
			}
		};
	}

	/**
	 * Registers gateway in Hawkbit
	 *
	 * @param gatewayId the gateway ID
	 * @param psk       the PSK
	 */
	public void registerGateway(String gatewayId, String psk) {
		var response = targetsApiClient.createTargets(
				Collections.singletonList(
						new TargetsCreationRequestPartVO()
								.controllerId(gatewayId)
								.name(gatewayId)
								.securityToken(psk)));
		switch (response.status()) {
			case CREATED -> {
				log.info("Created new gateway in Hawkbit with ID: {}", gatewayId);
			}
			case CONFLICT -> {
				log.debug("Gateway with ID {} already known to Hawkbit", gatewayId);
			}
			default -> {
				log.error(
						"Unable to create gateway with ID {} in Hawkbit. Got status code {}: {}",
						gatewayId,
						response.code(),
						response.body());
				throw new IllegalStateException("Unable to check if gateway is known to Hawkbit.");
			}
		}
	}
}
