package io.inoa.fleet.mqtt.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.inoa.fleet.mqtt.AbstractMqttTest;
import io.inoa.fleet.mqtt.MqttProperties;
import io.inoa.fleet.mqtt.MqttServiceClient;
import io.inoa.messaging.KafkaHeader;
import java.util.Map;
import java.util.UUID;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link ConnectionHandler}.
 *
 * @author stephan.schnabel@grayc.de
 */
@DisplayName("mqtt: handler connection")
public class ConnectionHandlerTest extends AbstractMqttTest {

  @DisplayName("connect & disconnect graceful")
  @Test
  void graceful(MqttProperties properties) throws MqttException {

    var url = "tcp://" + properties.getHost() + ":" + properties.getPort();
    var tenantId = "inoa";
    var gatewayId = "GW-0001";
    var psk = UUID.randomUUID().toString().getBytes();
    var client = new MqttServiceClient(url, tenantId, gatewayId, psk);
    var clientId = client.getClientId();

    client.connect();
    var record = kafka.awaitHonoEvent(tenantId, gatewayId);
    assertEquals(
        record.value(),
        Map.of("cause", "connected", "remote-id", clientId, "source", "inoa-mqtt"),
        "value");
    assertHeader(record, KafkaHeader.TENANT_ID, tenantId);
    assertHeader(record, KafkaHeader.DEVICE_ID, gatewayId);
    assertHeader(record, KafkaHeader.CONTENT_TYPE, KafkaHeader.CONTENT_TYPE_EVENT_DC);
    assertHeader(record, KafkaHeader.QOS, 1);

    client.disconnect();
    record = kafka.awaitHonoEvent(tenantId, gatewayId);
    assertEquals(
        record.value(),
        Map.of("cause", "disconnected", "remote-id", clientId, "source", "inoa-mqtt"),
        "value");
    assertHeader(record, KafkaHeader.TENANT_ID, tenantId);
    assertHeader(record, KafkaHeader.DEVICE_ID, gatewayId);
    assertHeader(record, KafkaHeader.CONTENT_TYPE, KafkaHeader.CONTENT_TYPE_EVENT_DC);
    assertHeader(record, KafkaHeader.QOS, 1);
  }

  @DisplayName("connect & disconnect hard")
  @Test
  void hard(MqttProperties properties) throws MqttException {

    var url = "tcp://" + properties.getHost() + ":" + properties.getPort();
    var tenantId = "inoa";
    var gatewayId = "GW-0001";
    var psk = UUID.randomUUID().toString().getBytes();
    var client = new MqttServiceClient(url, tenantId, gatewayId, psk);
    var clientId = client.getClientId();

    client.connect();
    var record = kafka.awaitHonoEvent(tenantId, gatewayId);
    assertEquals(
        record.value(),
        Map.of("cause", "connected", "remote-id", clientId, "source", "inoa-mqtt"),
        "value");
    assertHeader(record, KafkaHeader.TENANT_ID, tenantId);
    assertHeader(record, KafkaHeader.DEVICE_ID, gatewayId);
    assertHeader(record, KafkaHeader.CONTENT_TYPE, KafkaHeader.CONTENT_TYPE_EVENT_DC);
    assertHeader(record, KafkaHeader.QOS, 1);

    client.disconnectWithoutNotification();
    record = kafka.awaitHonoEvent(tenantId, gatewayId);
    assertEquals(
        record.value(),
        Map.of("cause", "disconnected", "remote-id", clientId, "source", "inoa-mqtt"),
        "value");
    assertHeader(record, KafkaHeader.TENANT_ID, tenantId);
    assertHeader(record, KafkaHeader.DEVICE_ID, gatewayId);
    assertHeader(record, KafkaHeader.CONTENT_TYPE, KafkaHeader.CONTENT_TYPE_EVENT_DC);
    assertHeader(record, KafkaHeader.QOS, 1);
  }
}
