package io.inoa.fleet.thing.rest;

import com.fasterxml.jackson.databind.JsonNode;

import io.inoa.fleet.thing.rest.management.ThingVO;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.annotation.Client;

@Client(id = "gateway-command")
public interface GatewayCommandClient {

	@Post("/command/{tenant_id}/{gateway_id}")
	io.micronaut.http.HttpResponse<ThingVO> sendGatewayCommand(@PathVariable(name = "tenant_id") String tenantId,
			@PathVariable(name = "gateway_id") String gatewayId, @Body JsonNode body);
}
