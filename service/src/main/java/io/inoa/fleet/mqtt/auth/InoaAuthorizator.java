package io.inoa.fleet.mqtt.auth;

import io.inoa.fleet.mqtt.MqttBroker;
import io.moquette.broker.security.IAuthorizatorPolicy;
import io.moquette.broker.subscriptions.Topic;
import jakarta.inject.Singleton;
import java.util.Set;

@Singleton
public class InoaAuthorizator implements IAuthorizatorPolicy {

  // https://www.eclipse.org/hono/docs/user-guide/mqtt-adapter/#receiving-commands-authenticated-device
  @Override
  public boolean canRead(Topic topic, String username, String clientId) {
    return topic.toString().startsWith(MqttBroker.COMMAND_TOPIC_LONG_NAME)
        || topic.toString().startsWith(MqttBroker.COMMAND_TOPIC_SHORT_NAME);
  }

  // https://www.eclipse.org/hono/docs/user-guide/mqtt-adapter/#publish-telemetry-data-authenticated-gateway
  // https://www.eclipse.org/hono/docs/user-guide/mqtt-adapter/#publish-an-event-authenticated-device
  @Override
  public boolean canWrite(Topic topic, String username, String clientId) {
    return Set.of(
                MqttBroker.TELEMETRY_TOPIC_SHORT_NAME,
                MqttBroker.TELEMETRY_TOPIC_LONG_NAME,
                MqttBroker.EVENT_TOPIC_SHORT_NAME,
                MqttBroker.EVENT_TOPIC_LONG_NAME)
            .contains(topic.toString())
        || topic.match(new Topic(MqttBroker.COMMAND_RESPONSE_TOPIC_LONG_MATCHER))
        || topic.match(new Topic(MqttBroker.COMMAND_RESPONSE_TOPIC_SHORT_MATCHER));
  }
}
