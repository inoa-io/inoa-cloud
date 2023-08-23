package io.inoa.fleet.remoting.rest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.inoa.fleet.AbstractTest;
import io.inoa.fleet.api.RemoteApiTestClient;
import io.inoa.fleet.api.RemoteApiTestSpec;
import io.inoa.fleet.model.RpcCommandVO;
import jakarta.inject.Inject;

@DisplayName("Remoting: REST")
public class RemoteControllerTest extends AbstractTest implements RemoteApiTestSpec {

	@Inject
	RemoteApiTestClient client;

	@Test
	@Disabled("Need to answer the command somehow")
	@Override
	public void sendRpcCommand200() throws Exception {
		var tenant = data.tenant("inoa");
		var auth = auth(tenant);
		// We need to answer the RPC command while we are waiting for the HTTP response.
		// Kind of tricky. We already have a test for the service, so this is skipped
		// for now.
		var response = client.sendRpcCommand(auth, "GW-0001", new RpcCommandVO().id("egal").method("rpc.list"));
		Assertions.assertNotNull(response);
	}

	@Override
	public void sendRpcCommand401() throws Exception {
	}
}
