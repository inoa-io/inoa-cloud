package io.kokuwa.fleet.registry;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.inject.Singleton;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import io.kokuwa.fleet.registry.domain.Gateway;
import io.kokuwa.fleet.registry.domain.GatewayGroup;
import io.kokuwa.fleet.registry.domain.GatewayGroup.GatewayGroupPK;
import io.kokuwa.fleet.registry.domain.GatewayGroupRepository;
import io.kokuwa.fleet.registry.domain.GatewayProperty;
import io.kokuwa.fleet.registry.domain.GatewayProperty.GatewayPropertyPK;
import io.kokuwa.fleet.registry.domain.GatewayPropertyRepository;
import io.kokuwa.fleet.registry.domain.GatewayRepository;
import io.kokuwa.fleet.registry.domain.Group;
import io.kokuwa.fleet.registry.domain.GroupRepository;
import io.kokuwa.fleet.registry.domain.Secret;
import io.kokuwa.fleet.registry.domain.SecretRepository;
import io.kokuwa.fleet.registry.domain.Tenant;
import io.kokuwa.fleet.registry.domain.TenantRepository;
import io.kokuwa.fleet.registry.rest.management.SecretTypeVO;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

/**
 * Class to create testdata.
 *
 * @author Stephan Schnabel
 */
@Singleton
@Transactional(TxType.REQUIRES_NEW)
@RequiredArgsConstructor
public class Data {

	private final TenantRepository tenantRepository;
	private final GroupRepository groupRepository;
	private final GatewayRepository gatewayRepository;
	private final GatewayGroupRepository gatewayGroupRepository;
	private final GatewayPropertyRepository gatewayPropertyRepository;
	private final SecretRepository secretRepository;
	private final ApplicationProperties applicationProperties;

	void deleteAll() {
		gatewayRepository.deleteAll();
		tenantRepository.deleteAll();
	}

	// jwt

	@SneakyThrows
	public KeyPair generateKeyPair() {
		return KeyPairGenerator.getInstance("RSA").generateKeyPair();
	}

	@SneakyThrows
	public String token(UUID gateway, String hmacSecret) {
		var jwt = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), tokenClaims(gateway));
		jwt.sign(new MACSigner(hmacSecret));
		return jwt.serialize();
	}

	public String token(UUID gateway, KeyPair rsaKeys) {
		return token(tokenClaims(gateway), rsaKeys);
	}

	@SneakyThrows
	public String token(JWTClaimsSet claims, KeyPair rsaKeys) {
		var jwt = new SignedJWT(new JWSHeader(JWSAlgorithm.RS256), claims);
		jwt.sign(new RSASSASigner(rsaKeys.getPrivate()));
		return jwt.serialize();
	}

	public JWTClaimsSet tokenClaims(UUID gateway) {
		return tokenClaims(gateway, Instant.now());
	}

	public JWTClaimsSet tokenClaims(UUID gateway, Instant now) {
		return new JWTClaimsSet.Builder().audience(applicationProperties.getGateway().getToken().getAudience())
				.jwtID(UUID.randomUUID().toString()).issuer(gateway.toString()).issueTime(Date.from(now))
				.notBeforeTime(Date.from(now)).expirationTime(Date.from(now)).build();
	}

	// manipulation

	public String tenantName() {
		return UUID.randomUUID().toString().substring(0, 20);
	}

	public Tenant tenant() {
		return tenant(tenantName(), true);
	}

	public Tenant tenant(String name) {
		return tenant(name, true);
	}

	public Tenant tenant(String name, boolean enabled) {
		return tenantRepository.save(new Tenant().setTenantId(UUID.randomUUID()).setName(name).setEnabled(enabled));
	}

	public String groupName() {
		return UUID.randomUUID().toString().substring(0, 20);
	}

	public Group group(Tenant tenant) {
		return group(tenant, groupName());
	}

	public Group group(Tenant tenant, String name) {
		return groupRepository.save(new Group().setGroupId(UUID.randomUUID()).setTenant(tenant).setName(name));
	}

	public String gatewayName() {
		return UUID.randomUUID().toString().substring(0, 32);
	}

	public Gateway gateway() {
		return gateway(tenant());
	}

	public Gateway gateway(Tenant tenant, Map<String, String> properties) {
		return gateway(tenant, List.of(), properties);
	}

	public Gateway gateway(Group group) {
		return gateway(group.getTenant(), gatewayName(), true, List.of(group));
	}

	public Gateway gateway(Tenant tenant) {
		return gateway(tenant, gatewayName(), true, List.of());
	}

	public Gateway gateway(Tenant tenant, String name) {
		return gateway(tenant, name, true, List.of());
	}

	public Gateway gateway(Tenant tenant, boolean enabled) {
		return gateway(tenant, gatewayName(), enabled, List.of());
	}

	public Gateway gateway(Tenant tenant, List<Group> groups, Map<String, String> properties) {
		var gateway = gateway(tenant, gatewayName(), true, groups);
		properties.forEach((key, value) -> property(gateway, key, value));
		return gateway;
	}

	public Gateway gateway(Tenant tenant, String name, boolean enabled, List<Group> groups) {
		Gateway gateway = gatewayRepository.save(
				new Gateway().setGatewayId(UUID.randomUUID()).setTenant(tenant).setName(name).setEnabled(enabled));
		if (!groups.isEmpty()) {
			gatewayGroupRepository.saveAll(groups.stream()
					.map(group -> new GatewayGroup().setPk(new GatewayGroupPK(gateway.getId(), group.getId())))
					.collect(Collectors.toSet()));
		}
		return gateway;
	}

	public GatewayProperty property(Gateway gateway, String key, String value) {
		return gatewayPropertyRepository
				.save(new GatewayProperty().setPk(new GatewayPropertyPK(gateway.getId(), key)).setValue(value));
	}

	public String secretName() {
		return UUID.randomUUID().toString().substring(0, 32);
	}

	public Secret secret(Gateway gateway) {
		return secretHmac(gateway, secretName(), UUID.randomUUID().toString());
	}

	public Secret secretHmac(Gateway gateway, String name, String hmac) {
		return secretRepository.save(new Secret().setSecretId(UUID.randomUUID()).setGateway(gateway).setName(name)
				.setEnabled(true).setType(SecretTypeVO.HMAC).setHmac(hmac.getBytes()));
	}

	public Secret secretRSA(Gateway gateway, String name, KeyPair keyPair) {
		return secretRSA(gateway, name, keyPair.getPublic().getEncoded(), keyPair.getPrivate().getEncoded());
	}

	public Secret secretRSA(Gateway gateway, String name, byte[] publicKey, byte[] privateKey) {
		return secretRepository.save(new Secret().setSecretId(UUID.randomUUID()).setGateway(gateway).setName(name)
				.setEnabled(true).setType(SecretTypeVO.RSA).setPublicKey(publicKey).setPrivateKey(privateKey));
	}

	// read

	public Long countTenants() {
		return tenantRepository.count();
	}

	public Long countGroups() {
		return groupRepository.count();
	}

	public Long countGateways() {
		return gatewayRepository.count();
	}

	public Long countSecrets(Gateway gateway) {
		return (long) secretRepository.findByGatewayOrderByName(gateway).size();
	}

	public Tenant find(Tenant tenant) {
		return tenantRepository.findById(tenant.getId()).get();
	}

	public Group find(Group group) {
		return groupRepository.findById(group.getId()).get().setTenant(group.getTenant());
	}

	public Gateway find(Gateway gateway) {
		return gatewayRepository.findByGatewayId(gateway.getGatewayId()).get();
	}

	public List<GatewayProperty> findProperties(Gateway gateway) {
		return gatewayPropertyRepository.findByGatewayId(gateway.getId());
	}
}
