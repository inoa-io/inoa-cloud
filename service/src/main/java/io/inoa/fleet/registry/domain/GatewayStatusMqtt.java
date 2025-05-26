package io.inoa.fleet.registry.domain;

import java.time.Instant;

import io.micronaut.data.annotation.Embeddable;
import io.micronaut.data.annotation.MappedProperty;
import lombok.Data;

@Embeddable
@Data
public class GatewayStatusMqtt {

	@MappedProperty("mqtt_connected")
	private Boolean connected;

	@MappedProperty("mqtt_timestamp")
	private Instant timestamp;
}
