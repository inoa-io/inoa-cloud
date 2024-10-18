package io.inoa.controller.mqtt.auth;

import io.inoa.controller.mqtt.MqttGatewayIdentifier;
import io.moquette.broker.security.IAuthenticator;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

@Singleton
@Slf4j
public class InoaAuthenticator implements IAuthenticator {

  @Override
  public boolean checkValid(String clientId, String username, byte[] password) {

    var gateway = MqttGatewayIdentifier.of(username);
    if (gateway == null) {
      return false;
    }

    log.error("Autorized {}/{}", gateway.tenantId(), gateway.gatewayId());

    return true;
  }
}
