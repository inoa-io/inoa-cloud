package io.inoa.fleet.thing.rest;

import com.fasterxml.jackson.databind.JsonNode;

import io.inoa.fleet.thing.rest.management.ThingVO;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.annotation.Client;

@Client(id = "gateway-command")
public interface GatewayCommandClient {

	@Post("/{tenantId}/{deviceId}/rpc")
	io.micronaut.http.HttpResponse<ThingVO> sendGatewayCommand(@PathVariable(name = "tenantId") String tenantId,
			@PathVariable(name = "deviceId") String gatewayId, @Body JsonNode body);
}
