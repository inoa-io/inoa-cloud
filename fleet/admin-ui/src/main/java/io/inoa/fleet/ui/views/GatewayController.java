package io.inoa.fleet.ui.views;

import java.util.List;

import javax.annotation.Nullable;

import io.inoa.fleet.registry.management.GatewayUpdateVO;
import io.inoa.fleet.registry.management.GatewaysApiClient;
import io.inoa.fleet.ui.internal.ClientWrapper;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.views.View;
import lombok.RequiredArgsConstructor;

@Controller
@Produces(MediaType.TEXT_HTML)
@RequiredArgsConstructor
public class GatewayController {

	private final GatewaysApiClient client;

	@View("gatewayList")
	@Get(uri = "/gateway")
	public GatewayListModel list(
			@QueryValue(value = "search") @Nullable String search,
			@QueryValue(value = "page", defaultValue = "1") int pageNumber,
			@QueryValue(value = "size", defaultValue = "20") int pageSize,
			@QueryValue(value = "sort", defaultValue = "name,asc") String sort) {
		var page = ClientWrapper
				.invoke("getGateways", () -> client.findGateways(pageNumber - 1, pageSize, List.of(sort), search))
				.body();
		return new GatewayListModel(pageNumber, pageSize, page, search, sort);
	}

	@View("gatewayDetail")
	@Get(uri = "/gateway/{gatewayId}")
	public GatewayDetailModel detail(@PathVariable String gatewayId) {
		return new GatewayDetailModel(ClientWrapper.invoke("GatewayController",
				() -> client.findGateway(gatewayId)).getBody());
	}

	@View("gatewayDetail")
	@Post(uri = "/gateway/{gatewayId}", consumes = MediaType.APPLICATION_FORM_URLENCODED)
	public GatewayDetailModel setEnabled(@PathVariable String gatewayId, Boolean enabled) {
		return new GatewayDetailModel(ClientWrapper.invoke("GatewayController",
				() -> client.updateGateway(gatewayId, new GatewayUpdateVO().enabled(enabled))).getBody());
	}
}
