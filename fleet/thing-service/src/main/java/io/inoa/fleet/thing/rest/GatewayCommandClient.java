package io.inoa.fleet.thing.rest;

import com.fasterxml.jackson.databind.JsonNode;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.annotation.Client;

@Client(id = "gateway-command")
public interface GatewayCommandClient {

	@Post("/{tenantId}/{deviceId}/rpc")
	HttpResponse<Void> sendGatewayCommand(@PathVariable(name = "tenantId") String tenantId,
			@PathVariable(name = "deviceId") String gatewayId, @Body JsonNode body);
}
