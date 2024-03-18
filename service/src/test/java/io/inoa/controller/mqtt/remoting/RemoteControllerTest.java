package io.inoa.controller.mqtt.remoting;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.inoa.rest.RemoteApiTestClient;
import io.inoa.rest.RemoteApiTestSpec;
import io.inoa.rest.RpcCommandVO;
import io.inoa.test.AbstractUnitTest;
import jakarta.inject.Inject;

@DisplayName("Remoting: REST")
public class RemoteControllerTest extends AbstractUnitTest implements RemoteApiTestSpec {

	@Inject RemoteApiTestClient client;

	@Disabled("Need to answer the command somehow")
	@Test
	@Override
	public void sendRpcCommand200() {
		var tenant = data.tenant("inoa");
		var auth = auth(tenant);
		// We need to answer the RPC command while we are waiting for the HTTP response.
		// Kind of tricky. We already have a test for the service, so this is skipped
		// for now.
		var response = client.sendRpcCommand(auth, "GW-0001", new RpcCommandVO().id("egal").method("rpc.list"));
		assertNotNull(response);
	}

	@Disabled("NYI")
	@Test
	@Override
	public void sendRpcCommand401() {}
}
