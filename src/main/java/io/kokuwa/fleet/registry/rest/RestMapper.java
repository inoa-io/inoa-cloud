package io.kokuwa.fleet.registry.rest;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import io.kokuwa.fleet.registry.domain.BaseEntity;
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

	@Mapping(target = "tenantId", source = "externalId")
	TenantVO toTenant(Tenant tenant);

	@Mapping(target = "tenantId", source = "tenant.externalId")
	@Mapping(target = "groupId", source = "externalId")
	GroupVO toGroup(Group group);

	@Mapping(target = "tenantId", source = "tenant.externalId")
	@Mapping(target = "gatewayId", source = "externalId")
	GatewayVO toGateway(Gateway gateway);

	@Mapping(target = "tenantId", source = "gateway.tenant.externalId")
	@Mapping(target = "gatewayId", source = "externalId")
	@Mapping(target = "groupIds", source = "groups")
	GatewayDetailVO toGatewayDetail(Gateway gateway);

	@Mapping(target = "gatewayId", source = "gateway.externalId")
	@Mapping(target = "secretId", source = "externalId")
	SecretVO toSecret(Secret secret);

	@Mapping(target = "gatewayId", source = "gateway.externalId")
	@Mapping(target = "secretId", source = "externalId")
	SecretDetailHmacVO toSecretDetailHmac(Secret secret);

	@Mapping(target = "gatewayId", source = "gateway.externalId")
	@Mapping(target = "secretId", source = "externalId")
	SecretDetailRSAVO toSecretDetailRSA(Secret secret);

	default SecretDetailVO toSecretDetail(Secret secret) {
		return secret.getType() == SecretTypeVO.HMAC
				? toSecretDetailHmac(secret)
				: toSecretDetailRSA(secret);
	}

	default UUID toId(BaseEntity entity) {
		return entity.getExternalId();
	}

	default Map<String, String> toMap(List<GatewayProperty> properties) {
		return properties == null ? Map.of()
				: properties.stream().collect(Collectors.toMap(
						property -> property.getPk().getKey(),
						property -> property.getValue(),
						(property1, property2) -> property1, TreeMap::new));
	}
}
