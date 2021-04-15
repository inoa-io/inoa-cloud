package io.kokuwa.fleet.registry.auth;

import java.util.Map;

import io.kokuwa.fleet.registry.domain.Gateway;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.security.authentication.Authentication;
import lombok.Getter;

/**
 * Authentication of a gateway.
 *
 * @author Stephan Schnabel
 */
@Introspected
@Getter
public class GatewayAuthentication implements Authentication {

	private static final long serialVersionUID = 1L;

	private final Gateway gateway;
	private final String name;
	private final Map<String, Object> attributes;

	public GatewayAuthentication(Gateway gateway) {
		this.gateway = gateway;
		this.name = "gateway:" + gateway.getExternalId().toString();
		this.attributes = Map.of();
	}
}
