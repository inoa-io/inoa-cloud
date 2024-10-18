package io.inoa.fleet.registry.auth;

import io.inoa.fleet.registry.domain.Gateway;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.security.authentication.Authentication;
import java.util.Map;
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

  private final transient Gateway gateway;
  private final String name;

  private final transient Map<String, Object> attributes;

  public GatewayAuthentication(Gateway gateway) {
    this.gateway = gateway;
    this.name = "gateway:" + gateway.getGatewayId();
    this.attributes = Map.of();
  }
}
