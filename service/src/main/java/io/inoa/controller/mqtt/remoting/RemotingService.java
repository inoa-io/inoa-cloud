package io.inoa.controller.mqtt.remoting;

import static io.inoa.controller.mqtt.MqttBroker.COMMAND_TOPIC_LONG_NAME;
import static java.nio.charset.StandardCharsets.UTF_8;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.inoa.controller.mqtt.MqttBroker;
import io.inoa.rest.RpcCommandVO;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.mqtt.MqttMessageBuilders;
import io.netty.handler.codec.mqtt.MqttQoS;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service for sending RPC commands to gateways.
 */
@Slf4j
@Singleton
@RequiredArgsConstructor
public class RemotingService {

	public static final String COMMAND_TOPIC_FORMAT_STRING = COMMAND_TOPIC_LONG_NAME + "/%s/%s/req/%s/cloudEventRpc";

	private final ObjectMapper mapper;
	private final MqttBroker mqttBroker;

	public void sendRpcCommand(String tenantId, String gatewayId, RpcCommandVO command) throws JsonProcessingException {
		log.trace("Sending command on tenant '{}' for gateway '{}' with message: {}", tenantId, gatewayId, command);

		// Create RPC message
		var commandJson = mapper.writeValueAsString(command);
		var topic = COMMAND_TOPIC_FORMAT_STRING.formatted(tenantId, gatewayId, command.getId());
		var message = MqttMessageBuilders.publish().topicName(topic).retained(true).qos(MqttQoS.AT_LEAST_ONCE)
				.payload(Unpooled.copiedBuffer(commandJson.getBytes(UTF_8))).build();

		// Send internal message
		var routingResults = mqttBroker.internalPublish(message, RemotingService.class.toString());
		if (routingResults.isAllFailed()) {
			log.error("Error sending command: {}", routingResults);
			throw new IllegalStateException("All MQTT routes failed.");
		}
	}
}
