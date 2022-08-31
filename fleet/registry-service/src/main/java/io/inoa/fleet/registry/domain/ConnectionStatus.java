package io.inoa.fleet.registry.domain;

import java.time.Instant;

import io.micronaut.data.annotation.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class ConnectionStatus {
	private boolean connected;

	private Instant timestamp;
}
