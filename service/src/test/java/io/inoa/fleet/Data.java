package io.inoa.fleet;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import jakarta.transaction.Transactional.TxType;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import io.inoa.fleet.registry.domain.*;
import io.inoa.rest.ConfigurationTypeVO;
import io.inoa.rest.CredentialTypeVO;
import io.inoa.rest.GatewayLocationDataVO;
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

	private static final Random random = new Random();

	private final TenantRepository tenantRepository;
	private final GroupRepository groupRepository;
	private final GatewayRepository gatewayRepository;
	private final GatewayGroupRepository gatewayGroupRepository;
	private final GatewayPropertyRepository gatewayPropertyRepository;
	private final CredentialRepository credentialRepository;
	private final ConfigurationDefinitionRepository configurationDefinitionRepository;
	private final TenantConfigurationRepository tenantConfigurationRepository;
	private final GroupConfigurationRepository groupConfigurationRepository;
	private final GatewayConfigurationRepository gatewayConfigurationRepository;
	private final FleetProperties fleetProperties;

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
		return new JWTClaimsSet.Builder()
				.audience(fleetProperties.getGateway().getToken().getAudience())
				.jwtID(UUID.randomUUID().toString())
				.issuer(gateway.toString())
				.issueTime(Date.from(now))
				.notBeforeTime(Date.from(now))
				.expirationTime(Date.from(now))
				.build();
	}

	// manipulation

	public String tenantId() {
		return UUID.randomUUID().toString().substring(0, 30);
	}

	public String tenantName() {
		return UUID.randomUUID().toString();
	}

	public Tenant tenant() {
		return tenant(tenantId(), true, false);
	}

	public GatewayLocationData getNullLocation() {
		var nullLocation = new GatewayLocationData();
		nullLocation.setHouseNumber(null);
		nullLocation.setRoad(null);
		nullLocation.setNeighbourhood(null);
		nullLocation.setSuburb(null);
		nullLocation.setCityDistrict(null);
		nullLocation.setCity(null);
		nullLocation.setState(null);
		nullLocation.setPostcode(null);
		nullLocation.setCountry(null);
		nullLocation.setCountryCode(null);
		nullLocation.setLatitude(null);
		nullLocation.setLongitude(null);

		return nullLocation;
	}

	public Tenant tenant(String tenantId) {
		return tenant(tenantId, true, false);
	}

	public Tenant tenant(String tenantId, boolean enabled, boolean deleted) {
		return tenantRepository.save(
				new Tenant()
						.setTenantId(tenantId)
						.setName(tenantName())
						.setEnabled(enabled)
						.setGatewayIdPattern("^ISRL02\\-[0-9]{12}$")
						.setCreated(Instant.now().truncatedTo(ChronoUnit.MILLIS))
						.setUpdated(Instant.now().truncatedTo(ChronoUnit.MILLIS))
						.setDeleted(deleted ? Instant.now().truncatedTo(ChronoUnit.MILLIS) : null));
	}

	public String groupName() {
		return UUID.randomUUID().toString().substring(0, 20);
	}

	public Group group(Tenant tenant) {
		return group(tenant, groupName());
	}

	public Group group(Tenant tenant, String name) {
		return groupRepository.save(
				new Group().setGroupId(UUID.randomUUID()).setTenant(tenant).setName(name));
	}

	public String gatewayId() {
		return "ISRL02-" + numeric(12);
	}

	public String gatewayName() {
		return UUID.randomUUID().toString().substring(0, 32);
	}

	public GatewayLocationDataVO gatewayLocationVO() {
		var location = new GatewayLocationDataVO();

		location.setLatitude(new Random().nextDouble() * 180 - 90);
		location.setLongitude(new Random().nextDouble() * 180 - 90);
		location.setRoad("Main Street Road");
		location.setHouseNumber("" + new Random().nextInt(1, 99));
		location.setPostcode("" + new Random().nextInt(10000, 99000));
		location.setCity("Royville");
		location.setCityDistrict("City Center");
		location.setNeighbourhood("Himitsu");
		location.setSuburb("Hanabi East");
		location.setState("Hikiboshi");
		location.setCountry("Japan");
		location.setCountryCode("JP");

		return location;
	}

	public GatewayLocationDataVO toGatewayLocationVO(GatewayLocationData locationData) {
		var location = new GatewayLocationDataVO();

		location.setLatitude(locationData.getLatitude());
		location.setLongitude(locationData.getLongitude());
		location.setRoad(locationData.getRoad());
		location.setHouseNumber(locationData.getHouseNumber());
		location.setPostcode(locationData.getPostcode());
		location.setCity(locationData.getCity());
		location.setCityDistrict(locationData.getCityDistrict());
		location.setNeighbourhood(locationData.getNeighbourhood());
		location.setSuburb(locationData.getSuburb());
		location.setState(locationData.getState());
		location.setCountry(locationData.getCountry());
		location.setCountryCode(locationData.getCountryCode());

		return location;
	}

	public GatewayLocationData toGatewayLocation(GatewayLocationDataVO locationData) {
		var location = new GatewayLocationData();

		location.setLatitude(locationData.getLatitude());
		location.setLongitude(locationData.getLongitude());
		location.setRoad(locationData.getRoad());
		location.setHouseNumber(locationData.getHouseNumber());
		location.setPostcode(locationData.getPostcode());
		location.setCity(locationData.getCity());
		location.setCityDistrict(locationData.getCityDistrict());
		location.setNeighbourhood(locationData.getNeighbourhood());
		location.setSuburb(locationData.getSuburb());
		location.setState(locationData.getState());
		location.setCountry(locationData.getCountry());
		location.setCountryCode(locationData.getCountryCode());

		return location;
	}

	public GatewayLocationData gatewayLocation() {
		var location = new GatewayLocationData();

		location.setLatitude(new Random().nextDouble() * 180 - 90);
		location.setLongitude(new Random().nextDouble() * 180 - 90);
		location.setRoad("Main Street Road");
		location.setHouseNumber("" + new Random().nextInt(1, 99));
		location.setPostcode("" + new Random().nextInt(10000, 99000));
		location.setCity("Royville");
		location.setCityDistrict("City Center");
		location.setNeighbourhood("Himitsu");
		location.setSuburb("Hanabi East");
		location.setState("Hikiboshi");
		location.setCountry("Japan");
		location.setCountryCode("JP");

		return location;
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

	public Gateway gateway(Tenant tenant, GatewayLocationData location) {
		return gateway(tenant, gatewayName(), location, true, List.of());
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
		var gateway = gatewayRepository.save(
				new Gateway()
						.setGatewayId(gatewayId())
						.setTenant(tenant)
						.setName(name)
						.setEnabled(enabled)
						.setStatus(
								new GatewayStatus().setMqtt(new GatewayStatusMqtt().setConnected(false))));
		if (!groups.isEmpty()) {
			gatewayGroupRepository.saveAll(
					groups.stream()
							.map(group -> new GatewayGroup().setGateway(gateway).setGroup(group))
							.collect(Collectors.toSet()));
		}
		return gateway;
	}

	public Gateway gateway(
			Tenant tenant,
			String name,
			GatewayLocationData location,
			boolean enabled,
			List<Group> groups) {
		var gateway = gatewayRepository.save(
				new Gateway()
						.setGatewayId(gatewayId())
						.setTenant(tenant)
						.setName(name)
						.setLocation(location)
						.setEnabled(enabled)
						.setStatus(
								new GatewayStatus().setMqtt(new GatewayStatusMqtt().setConnected(false))));
		if (!groups.isEmpty()) {
			gatewayGroupRepository.saveAll(
					groups.stream()
							.map(group -> new GatewayGroup().setGateway(gateway).setGroup(group))
							.collect(Collectors.toSet()));
		}
		return gateway;
	}

	public GatewayProperty property(Gateway gateway, String key, String value) {
		return gatewayPropertyRepository.save(
				new GatewayProperty().setGateway(gateway).setKey(key).setValue(value));
	}

	public String credentialName() {
		return UUID.randomUUID().toString().substring(0, 32);
	}

	public Credential credentialPSK(Gateway gateway) {
		return credential(gateway, CredentialTypeVO.PSK, UUID.randomUUID().toString().getBytes());
	}

	public Credential credentialInitialPSK(Gateway gateway) {
		var credential = new Credential()
				.setGateway(gateway)
				.setCredentialId(UUID.randomUUID())
				.setName("initial")
				.setEnabled(true)
				.setType(CredentialTypeVO.PSK)
				.setValue(UUID.randomUUID().toString().getBytes());
		return credentialRepository.save(credential);
	}

	@SneakyThrows
	public Credential credentialRSA(Gateway gateway, KeyPair keyPair) {
		return credential(gateway, CredentialTypeVO.RSA, keyPair.getPublic().getEncoded());
	}

	public Credential credential(Gateway gateway, CredentialTypeVO type, byte[] value) {
		var credential = new Credential()
				.setGateway(gateway)
				.setCredentialId(UUID.randomUUID())
				.setName(credentialName())
				.setEnabled(true)
				.setType(type)
				.setValue(value);
		return credentialRepository.save(credential);
	}

	public ConfigurationDefinition definition(String key, ConfigurationTypeVO type) {
		return definition(key, type, d -> {});
	}

	public ConfigurationDefinition definition(
			String key, ConfigurationTypeVO type, Consumer<ConfigurationDefinition> consumer) {
		var definition = new ConfigurationDefinition().setKey(key).setType(type);
		consumer.accept(definition);
		return configurationDefinitionRepository.save(definition);
	}

	public Configuration configuration(
			ConfigurationDefinition definition, Tenant tenant, String value) {
		return tenantConfigurationRepository.save(
				new TenantConfiguration().setDefinition(definition).setTenant(tenant).setValue(value));
	}

	public Configuration configuration(
			Group group, ConfigurationDefinition definition, String value) {
		return groupConfigurationRepository.save(
				new GroupConfiguration().setGroup(group).setDefinition(definition).setValue(value));
	}

	public Configuration configuration(
			Gateway gateway, ConfigurationDefinition definition, String value) {
		return gatewayConfigurationRepository.save(
				new GatewayConfiguration().setGateway(gateway).setDefinition(definition).setValue(value));
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

	public Long countCredentials(Gateway gateway) {
		return (long) credentialRepository.findByGateway(gateway).size();
	}

	public Long countDefinitions(Tenant tenant) {
		return (long) configurationDefinitionRepository.findAllOrderByKey().size();
	}

	public Tenant find(Tenant tenant) {
		return tenantRepository.findById(tenant.getId()).get();
	}

	public Tenant findTenant(String tenantId) {
		return tenantRepository.findByTenantId(tenantId).orElse(null);
	}

	public Group find(Group group) {
		return groupRepository.findById(group.getId()).get().setTenant(group.getTenant());
	}

	public Gateway find(Gateway gateway) {
		return gatewayRepository.findByGatewayId(gateway.getGatewayId()).get();
	}

	public Gateway findGateway(String gatewayId) {
		return gatewayRepository.findByGatewayId(gatewayId).orElse(null);
	}

	public Credential find(Credential credential) {
		return credentialRepository
				.findByGatewayAndCredentialId(credential.getGateway(), credential.getCredentialId())
				.get();
	}

	public List<Credential> findCredentials(Gateway gateway) {
		return credentialRepository.findByGateway(gateway);
	}

	public ConfigurationDefinition findConfigurationDefinition(Tenant tenant, String key) {
		return configurationDefinitionRepository.findByKey(key).orElse(null);
	}

	public String findConfigurationValue(ConfigurationDefinition definition) {
		return tenantConfigurationRepository
				.findByDefinition(definition)
				.map(TenantConfiguration::getValue)
				.orElse(null);
	}

	public List<GatewayProperty> findProperties(Gateway gateway) {
		return gatewayPropertyRepository.findByGateway(gateway);
	}

	private String numeric(int length) {
		return random.ints(0, 9).mapToObj(String::valueOf).limit(length).collect(Collectors.joining());
	}
}
