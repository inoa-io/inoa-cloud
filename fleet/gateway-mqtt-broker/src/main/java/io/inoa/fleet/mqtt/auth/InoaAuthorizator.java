package io.inoa.fleet.mqtt.auth;

import java.util.Set;

import io.moquette.broker.security.IAuthorizatorPolicy;
import io.moquette.broker.subscriptions.Topic;
import jakarta.inject.Singleton;

@Singleton
public class InoaAuthorizator implements IAuthorizatorPolicy {

	// https://www.eclipse.org/hono/docs/user-guide/mqtt-adapter/#receiving-commands-authenticated-device
	@Override
	public boolean canRead(Topic topic, String username, String clientId) {
		return false;
	}

	// https://www.eclipse.org/hono/docs/user-guide/mqtt-adapter/#publish-telemetry-data-authenticated-gateway
	// https://www.eclipse.org/hono/docs/user-guide/mqtt-adapter/#publish-an-event-authenticated-device
	@Override
	public boolean canWrite(Topic topic, String username, String clientId) {
		return Set.of("t", "telemetry", "e", "event").contains(topic.toString());
	}
}
