package io.inoa.fleet.remoting;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import jakarta.inject.Singleton;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.inoa.fleet.mqtt.MqttBroker;
import io.inoa.rest.RpcCommandVO;
import io.inoa.rest.RpcResponseVO;
import io.moquette.interception.AbstractInterceptHandler;
import io.moquette.interception.messages.InterceptPublishMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/** Listener for RPC command responses. */
@Singleton
@Slf4j
@AllArgsConstructor
public class RemotingHandler extends AbstractInterceptHandler {

	private final ObjectMapper mapper;
	private final Map<String, RpcMessage> commandBuffer = new HashMap<>();

	@Override
	public void onPublish(InterceptPublishMessage message) {
		// Check if this message is an RPC response
		if (!MqttBroker.matchesTopic(
				message.getTopicName(), MqttBroker.COMMAND_RESPONSE_TOPIC_LONG_MATCHER)
				&& !MqttBroker.matchesTopic(
						message.getTopicName(), MqttBroker.COMMAND_RESPONSE_TOPIC_SHORT_MATCHER)) {
			log.trace("Ignoring RPC message on topic: {}", message.getTopicName());
			return;
		}

		// Get payload
		var payload = new byte[message.getPayload().readableBytes()];
		message.getPayload().readBytes(payload);
		log.trace("Received command message: {}", new String(payload));

		// Put parsed response on stack
		try {
			var rpcResponse = mapper.readValue(new String(payload), RpcResponseVO.class);
			putCommandResponse(rpcResponse.getId(), rpcResponse);
		} catch (JsonProcessingException e) {
			log.error("Dropping unparsable command response: {}", new String(payload));
		} finally {
			message.getPayload().release();
		}
	}

	public Optional<RpcResponseVO> getCommandResponse(String commandId) {
		if (!commandBuffer.containsKey(commandId) || commandBuffer.get(commandId) == null) {
			return Optional.empty();
		}
		return commandBuffer.get(commandId).rpcResponse;
	}

	private void putCommandResponse(String commandId, RpcResponseVO response) {
		if (!commandBuffer.containsKey(commandId)) {
			log.warn("Got response of command that was never sent: {}", response);
			// We will not stack "dead letters" here, because housekeeping will be hard
			return;
		}
		var message = commandBuffer.get(commandId);
		if (message.rpcResponse.isPresent()) {
			log.warn("Got more than one RPC response for command ID: {}", commandId);
		}
		commandBuffer.put(commandId,
				new RpcMessage(message.sentAt, message.ttl, message.rpcCommand, Optional.of(response)));
	}

	public void putCommandMessage(String commandId, RpcMessage message) {
		commandBuffer.put(commandId, message);
	}

	@Override
	public String getID() {
		return this.getClass().getName();
	}

	@Override
	public void onSessionLoopError(Throwable error) {
		log.warn("Got session loop error", error);
	}

	record RpcMessage(long sentAt, long ttl, RpcCommandVO rpcCommand, Optional<RpcResponseVO> rpcResponse) {};
}
