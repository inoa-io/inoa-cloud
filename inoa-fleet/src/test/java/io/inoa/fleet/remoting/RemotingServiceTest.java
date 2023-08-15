package io.inoa.fleet.remoting;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.awaitility.Awaitility;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.inoa.fleet.broker.AbstractMqttTest;
import io.inoa.fleet.broker.MqttProperties;
import io.inoa.fleet.broker.MqttServiceClient;
import io.inoa.fleet.broker.TestMqttListener;
import io.inoa.fleet.model.RpcCommandVO;
import io.inoa.fleet.model.RpcResponseVO;
import io.inoa.fleet.remoting.service.RemotingHandler;
import io.inoa.fleet.remoting.service.RemotingService;
import jakarta.inject.Inject;

class RemotingServiceTest extends AbstractMqttTest {

	@Inject
	MqttProperties properties;

	@Inject
	RemotingService remotingService;

	@Inject
	RemotingHandler remotingHandler;

	@DisplayName("Successful RPC command")
	@Test
	void rpcCommand(TestMqttListener mqttListener) throws MqttException, JsonProcessingException {

		// Test constants
		final var url = "ssl://" + properties.getHost() + ":" + properties.getTls().getPort();
		final var tenantId = "inoa";
		final var gatewayId = "GW-0001";
		final var psk = UUID.randomUUID().toString().getBytes();

		// Create client to act as gateway
		var fakeGatewayClient = new MqttServiceClient(url, tenantId, gatewayId, psk);
		fakeGatewayClient.trustAllCertificates().connect();

		// Subscribe to RPC commands for our gateway and tenant
		fakeGatewayClient.subscribe("command/inoa/GW-0001/req/+/cloudEventRpc", mqttListener);
		// Clear all received messages
		mqttListener.clear();

		// Send our RPC command
		remotingService.sendRpcCommand(tenantId, gatewayId, new RpcCommandVO().id("blub").method("rpc.list"));

		// Wait until our fake gateway "sees" the command
		var record = mqttListener.await();
		var command = new ObjectMapper().readValue(new String(record.getPayload()), RpcCommandVO.class);

		// Send the command response from our fake gateway
		fakeGatewayClient.publish("command/inoa/GW-0001/res/" + command.getId() + "/200",
				new ObjectMapper().writeValueAsBytes(
						new RpcResponseVO().id(command.getId()).result(Collections.singletonList("dp.get"))));

		// Wait for the response to arrive
		Awaitility.await().pollDelay(500, TimeUnit.MILLISECONDS)
				.until(() -> remotingHandler.getCommandResponse(command.getId()).isPresent());

		// Check if the ID and result is as expected
		var response = remotingHandler.getCommandResponse(command.getId()).get();
		Assertions.assertEquals(command.getId(), response.getId());
		Assertions.assertEquals("[dp.get]", response.getResult().toString());

		fakeGatewayClient.disconnect();
	}
}
