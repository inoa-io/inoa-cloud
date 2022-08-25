package io.inoa.fleet.ui.views;

import java.util.Optional;

import io.inoa.fleet.registry.management.GatewayDetailVO;
import io.micronaut.core.annotation.Introspected;
import lombok.Getter;

@Introspected
@Getter
public class GatewayDetailModel {

	private final GatewayDetailVO gateway;

	public GatewayDetailModel(Optional<GatewayDetailVO> gateway) {
		this.gateway = gateway.orElse(null);
	}
}
