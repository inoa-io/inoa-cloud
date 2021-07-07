package io.kokuwa.fleet.registry.rest;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import io.kokuwa.fleet.registry.domain.Gateway;
import io.kokuwa.fleet.registry.domain.GatewayProperty;
import io.kokuwa.fleet.registry.domain.Group;
import io.kokuwa.fleet.registry.domain.Secret;
import io.kokuwa.fleet.registry.domain.Tenant;
import io.kokuwa.fleet.registry.rest.management.GatewayDetailVO;
import io.kokuwa.fleet.registry.rest.management.GatewayVO;
import io.kokuwa.fleet.registry.rest.management.GroupVO;
import io.kokuwa.fleet.registry.rest.management.SecretDetailHmacVO;
import io.kokuwa.fleet.registry.rest.management.SecretDetailRSAVO;
import io.kokuwa.fleet.registry.rest.management.SecretDetailVO;
import io.kokuwa.fleet.registry.rest.management.SecretTypeVO;
import io.kokuwa.fleet.registry.rest.management.SecretVO;
import io.kokuwa.fleet.registry.rest.management.TenantVO;

@Mapper(componentModel = "jsr330")
public interface RestMapper {

	TenantVO toTenant(Tenant tenant);

	GroupVO toGroup(Group group);

	GatewayVO toGateway(Gateway gateway);

	@Mapping(target = "groupIds", source = "groups")
	GatewayDetailVO toGatewayDetail(Gateway gateway);

	@Mapping(target = "gatewayId", source = "secret.gateway.gatewayId")
	SecretVO toSecret(Secret secret);

	@Mapping(target = "gatewayId", source = "secret.gateway.gatewayId")
	SecretDetailHmacVO toSecretDetailHmac(Secret secret);

	@Mapping(target = "gatewayId", source = "secret.gateway.gatewayId")
	SecretDetailRSAVO toSecretDetailRSA(Secret secret);

	default SecretDetailVO toSecretDetail(Secret secret) {
		return secret.getType() == SecretTypeVO.HMAC
				? toSecretDetailHmac(secret)
				: toSecretDetailRSA(secret);
	}

	default UUID toGroupId(Group group) {
		return group.getGroupId();
	}

	default Map<String, String> toMap(List<GatewayProperty> properties) {
		return properties == null ? Map.of()
				: properties.stream().collect(Collectors.toMap(
						property -> property.getPk().getKey(),
						property -> property.getValue(),
						(property1, property2) -> property1, TreeMap::new));
	}
}
