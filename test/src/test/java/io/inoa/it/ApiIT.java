package io.inoa.it;

import static io.inoa.test.HttpAssertions.assert200;
import static io.inoa.test.HttpAssertions.assert204;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;

import io.inoa.rest.CredentialTypeVO;
import io.inoa.rest.GatewayApiTestClient;
import io.inoa.rest.GatewayVO;
import io.inoa.rest.RegisterVO;
import io.inoa.test.AbstractIntegrationTest;
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpRequest;

@DisplayName("generic test for api")
public class ApiIT extends AbstractIntegrationTest {

	@DisplayName("check timestamp")
	@Test
	void timestamp(GatewayApiTestClient gatewayClient) {

		// create gateway to have gateway with created timestamp

		var gatewayId = gatewayId();
		assert204(() -> gatewayClient.register(new RegisterVO()
				.gatewayId(gatewayId)
				.credentialType(CredentialTypeVO.PSK)
				.credentialValue(new byte[0])),
				"failed to register gateway");

		// get gateway and check tpye of timestamp

		var request = HttpRequest.GET("/gateways/" + gatewayId).header(HttpHeaders.AUTHORIZATION, keycloak.admin());
		var response = assert200(() -> client.toBlocking().exchange(request, JsonNode.class));
		var timestamp = response.get(GatewayVO.JSON_PROPERTY_CREATED);
		assertEquals(JsonNodeType.STRING, timestamp.getNodeType(), "timestamp type invalid");
		assertDoesNotThrow(() -> Instant.parse(timestamp.asText()));
	}
}
