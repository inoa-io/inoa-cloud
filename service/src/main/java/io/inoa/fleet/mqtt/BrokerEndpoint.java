package io.inoa.fleet.mqtt;

import jakarta.inject.Inject;

import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.exceptions.HttpStatusException;
import io.micronaut.management.endpoint.annotation.Endpoint;
import io.micronaut.management.endpoint.annotation.Read;
import lombok.RequiredArgsConstructor;

/**
 * Micronaut endpoint for MQTT broker status.
 *
 * @author fabian.schlegel@grayc.de
 */
@Endpoint("broker")
@RequiredArgsConstructor
public class BrokerEndpoint {

	@Inject
	MqttBroker mqttBroker;

	/**
	 * Trigger consumption calculation
	 *
	 * <pre>
	 * curl -v "http://127.0.0.1:8080/endpoints/broker"
	 * </pre>
	 */
	@Read(produces = MediaType.TEXT_PLAIN)
	String status() {
		try {
			StringBuilder result = new StringBuilder();
			mqttBroker.getConnectedClients().forEach(client -> result.append(client.toString()).append("\n"));
			return result.toString();
		} catch (Exception e) {
			throw new HttpStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}
}
