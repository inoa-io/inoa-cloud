package io.inoa.fleet.mqtt.handler;

import io.inoa.fleet.mqtt.Gateway;
import io.moquette.interception.AbstractInterceptHandler;
import io.moquette.interception.messages.InterceptConnectMessage;
import io.moquette.interception.messages.InterceptConnectionLostMessage;
import io.moquette.interception.messages.InterceptDisconnectMessage;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

@Singleton
@Slf4j
public class ConnectionHandler extends AbstractInterceptHandler {

	@Override
	public String getID() {
		return "connection";
	}

	@Override
	public Class<?>[] getInterceptedMessageTypes() {
		return new Class[] {
				InterceptConnectMessage.class,
				InterceptDisconnectMessage.class,
				InterceptConnectionLostMessage.class };
	}

	@Override
	public void onConnect(InterceptConnectMessage message) {
		Gateway.of(message.getUsername()).mdc(gateway -> log.debug("Gateway connected"));
	}

	@Override
	public void onDisconnect(InterceptDisconnectMessage message) {
		Gateway.of(message.getUsername()).mdc(gateway -> log.debug("Gateway disconnected"));
	}

	@Override
	public void onConnectionLost(InterceptConnectionLostMessage message) {
		Gateway.of(message.getUsername()).mdc(gateway -> log.debug("Gateway lost connection"));
	}
}
