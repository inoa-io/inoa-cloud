package io.kokuwa.fleet.registry.rest;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import io.kokuwa.fleet.registry.domain.Credential;
import io.kokuwa.fleet.registry.domain.Gateway;
import io.kokuwa.fleet.registry.domain.GatewayProperty;
import io.kokuwa.fleet.registry.domain.Group;
import io.kokuwa.fleet.registry.domain.Secret;
import io.kokuwa.fleet.registry.domain.Tenant;
import io.kokuwa.fleet.registry.rest.management.CredentialCreateVO;
import io.kokuwa.fleet.registry.rest.management.CredentialVO;
import io.kokuwa.fleet.registry.rest.management.GatewayDetailVO;
import io.kokuwa.fleet.registry.rest.management.GatewayVO;
import io.kokuwa.fleet.registry.rest.management.GroupVO;
import io.kokuwa.fleet.registry.rest.management.SecretCreatePSKVO;
import io.kokuwa.fleet.registry.rest.management.SecretCreatePasswordVO;
import io.kokuwa.fleet.registry.rest.management.SecretCreateRSAVO;
import io.kokuwa.fleet.registry.rest.management.SecretCreateVO;
import io.kokuwa.fleet.registry.rest.management.SecretDetailPSKVO;
import io.kokuwa.fleet.registry.rest.management.SecretDetailPasswordVO;
import io.kokuwa.fleet.registry.rest.management.SecretDetailRSAVO;
import io.kokuwa.fleet.registry.rest.management.SecretDetailVO;
import io.kokuwa.fleet.registry.rest.management.SecretVO;
import io.kokuwa.fleet.registry.rest.management.TenantVO;

@Mapper(componentModel = "jsr330")
public interface RestMapper {

	TenantVO toTenant(Tenant tenant);

	GroupVO toGroup(Group group);

	List<GroupVO> toGroups(List<Group> groups);

	GatewayVO toGateway(Gateway gateway);

	List<GatewayVO> toGateways(List<Gateway> gateways);

	@Mapping(target = "groupIds", source = "groups")
	GatewayDetailVO toGatewayDetail(Gateway gateway);

//	@Mapping(target = "gatewayId", source = "secret.gateway.gatewayId")
//	SecretVO toSecret(Secret secret);
//
//	@Mapping(target = "gatewayId", source = "secret.gateway.gatewayId")
//	SecretDetailHmacVO toSecretDetailHmac(Secret secret);
//
//	@Mapping(target = "gatewayId", source = "secret.gateway.gatewayId")
//	SecretDetailRSAVO toSecretDetailRSA(Secret secret);
//
//	default SecretDetailVO toSecretDetail(Secret secret) {
//		return secret.getType() == SecretTypeVO.HMAC
//				? toSecretDetailHmac(secret)
//				: toSecretDetailRSA(secret);
//	}

	CredentialVO toCredential(Credential credential);

	List<CredentialVO> toCredentials(List<Credential> gateway);

	@Mapping(target = "type", source = "credential.type")
	SecretVO toSecret(Secret secret);

	default SecretDetailVO toSecretDetail(Secret secret) {
		switch (secret.getCredential().getType()) {
			case PSK:
				return toSecretDetailPSK(secret);
			case RSA:
				return toSecretDetailRSA(secret);
			case PASSWORD:
				return toSecretDetailPassword(secret);
			default:
				throw new IllegalArgumentException("Unsupported type: " + secret.getCredential().getType());
		}
	}

	@Named("toSecretDetailPassword")
	SecretDetailPasswordVO toSecretDetailPassword(Secret secret);

	@Named("toSecretDetailPSK")
	SecretDetailPSKVO toSecretDetailPSK(Secret secret);

	@Named("toSecretDetailRSA")
	SecretDetailRSAVO toSecretDetailRSA(Secret secret);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "credentialId", expression = "java(java.util.UUID.randomUUID())")
	@Mapping(target = "enabled", source = "credential.enabled")
	@Mapping(target = "created", ignore = true)
	@Mapping(target = "updated", ignore = true)
	Credential toCredential(Gateway gateway, CredentialCreateVO credential);

	default Secret toSecret(SecretCreateVO secret) {
		return toSecret(null, secret);
	}

	default Secret toSecret(Credential credential, SecretCreateVO secret) {
		switch (secret.getType()) {
			case PSK:
				return toSecret(credential, (SecretCreatePSKVO) secret);
			case RSA:
				return toSecret(credential, (SecretCreateRSAVO) secret);
			case PASSWORD:
				return toSecret(credential, (SecretCreatePasswordVO) secret);
			default:
				throw new IllegalArgumentException("Unsupported type: " + secret.getType());
		}
	}

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "secretId", expression = "java(java.util.UUID.randomUUID())")
	@Mapping(target = "privateKey", ignore = true)
	@Mapping(target = "publicKey", ignore = true)
	@Mapping(target = "secret", ignore = true)
	@Mapping(target = "created", ignore = true)
	Secret toSecret(Credential credential, SecretCreatePasswordVO secret);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "secretId", expression = "java(java.util.UUID.randomUUID())")
	@Mapping(target = "secret", ignore = true)
	@Mapping(target = "password", ignore = true)
	@Mapping(target = "created", ignore = true)
	Secret toSecret(Credential credential, SecretCreateRSAVO secret);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "secretId", expression = "java(java.util.UUID.randomUUID())")
	@Mapping(target = "privateKey", ignore = true)
	@Mapping(target = "publicKey", ignore = true)
	@Mapping(target = "password", ignore = true)
	@Mapping(target = "created", ignore = true)
	Secret toSecret(Credential credential, SecretCreatePSKVO secret);

	default UUID toGroupId(Group group) {
		return group.getGroupId();
	}

	default Map<String, String> toMap(List<GatewayProperty> properties) {
		return properties == null ? Map.of()
				: properties.stream().collect(Collectors.toMap(
						GatewayProperty::getKey,
						GatewayProperty::getValue,
						(property1, property2) -> property1, TreeMap::new));
	}
}
