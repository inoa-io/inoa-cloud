package io.inoa.controller.mqtt.auth;

import static io.inoa.controller.mqtt.MqttBroker.COMMAND_RESPONSE_TOPIC_LONG_MATCHER;
import static io.inoa.controller.mqtt.MqttBroker.COMMAND_RESPONSE_TOPIC_SHORT_MATCHER;
import static io.inoa.controller.mqtt.MqttBroker.COMMAND_TOPIC_LONG_NAME;
import static io.inoa.controller.mqtt.MqttBroker.COMMAND_TOPIC_SHORT_NAME;
import static io.inoa.controller.mqtt.MqttBroker.EVENT_TOPIC_LONG_NAME;
import static io.inoa.controller.mqtt.MqttBroker.EVENT_TOPIC_SHORT_NAME;
import static io.inoa.controller.mqtt.MqttBroker.TELEMETRY_TOPIC_LONG_NAME;
import static io.inoa.controller.mqtt.MqttBroker.TELEMETRY_TOPIC_SHORT_NAME;

import java.util.Set;

import io.moquette.broker.security.IAuthorizatorPolicy;
import io.moquette.broker.subscriptions.Topic;
import jakarta.inject.Singleton;

@Singleton
public class InoaAuthorizator implements IAuthorizatorPolicy {

	// https://www.eclipse.org/hono/docs/user-guide/mqtt-adapter/#receiving-commands-authenticated-device
	@Override
	public boolean canRead(Topic topic, String username, String clientId) {
		return topic.toString().startsWith(COMMAND_TOPIC_LONG_NAME)
				|| topic.toString().startsWith(COMMAND_TOPIC_SHORT_NAME);
	}

	// https://www.eclipse.org/hono/docs/user-guide/mqtt-adapter/#publish-telemetry-data-authenticated-gateway
	// https://www.eclipse.org/hono/docs/user-guide/mqtt-adapter/#publish-an-event-authenticated-device
	@Override
	public boolean canWrite(Topic topic, String username, String clientId) {
		return Set
				.of(TELEMETRY_TOPIC_SHORT_NAME, TELEMETRY_TOPIC_LONG_NAME, EVENT_TOPIC_SHORT_NAME,
						EVENT_TOPIC_LONG_NAME)
				.contains(topic.toString()) || topic.match(new Topic(COMMAND_RESPONSE_TOPIC_LONG_MATCHER))
				|| topic.match(new Topic(COMMAND_RESPONSE_TOPIC_SHORT_MATCHER));
	}
}
