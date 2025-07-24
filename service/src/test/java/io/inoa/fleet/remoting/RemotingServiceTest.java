package io.inoa.fleet.remoting;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.Collections;

import jakarta.inject.Inject;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.inoa.fleet.mqtt.MqttProperties;
import io.inoa.fleet.mqtt.MqttServiceClient;
import io.inoa.rest.RpcCommandVO;
import io.inoa.rest.RpcResponseVO;
import io.inoa.test.AbstractUnitTest;
import io.inoa.test.Await;

public class RemotingServiceTest extends AbstractUnitTest {

	@Inject
	MqttProperties properties;
	@Inject
	RemotingService remotingService;
	@Inject
	RemotingHandler remotingHandler;

	@DisplayName("Successful RPC command")
	@Test
	void rpcCommand(TestMqttListener mqttListener) throws MqttException, IOException {

		var tenant = data.tenant("test");
		var gateway = data.gateway(tenant, true);
		var credential = data.credentialInitialPSK(gateway);

		// Test constants
		var url = "ssl://" + properties.getHost() + ":" + properties.getTls().getPort();
		var tenantId = tenant.getTenantId();
		var gatewayId = gateway.getGatewayId();
		var psk = credential.getValue();

		// Create client to act as gateway
		var fakeGatewayClient = new MqttServiceClient(url, tenantId, gatewayId, psk);
		fakeGatewayClient.trustAllCertificates().connect();

		// Subscribe to RPC commands for our gateway and tenant
		fakeGatewayClient.subscribe("command/" + tenantId + "/" + gatewayId + "/req/+/cloudEventRpc", mqttListener);
		// Clear all received messages
		mqttListener.clear();

		// Send our RPC command
		remotingService.sendRpcCommand(
				tenantId, gatewayId, new RpcCommandVO().id("blub").method("rpc.list"));

		// Wait until our fake gateway "sees" the command
		var record = mqttListener.await();
		var command = mapper.readValue(record.getPayload(), RpcCommandVO.class);

		// Send the command response from our fake gateway
		fakeGatewayClient.publish(
				"command/" + tenantId + "/" + gatewayId + "/res/" + command.getId() + "/200",
				new ObjectMapper()
						.writeValueAsBytes(
								new RpcResponseVO()
										.id(command.getId())
										.result(Collections.singletonList("dp.get"))));

		// Wait for the response to arrive
		Await.await(log, "wait for command respoinse")
				.until(() -> remotingHandler.getCommandResponse(command.getId()).isPresent());

		// Check if the ID and result is as expected
		var response = remotingHandler.getCommandResponse(command.getId()).get();
		assertEquals(command.getId(), response.getId());
		assertEquals("[dp.get]", response.getResult().toString());

		fakeGatewayClient.disconnect();
	}
}
