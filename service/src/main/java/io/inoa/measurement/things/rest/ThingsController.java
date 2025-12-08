package io.inoa.measurement.things.rest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import jakarta.validation.Valid;

import io.inoa.fleet.registry.domain.Gateway;
import io.inoa.fleet.registry.domain.GatewayRepository;
import io.inoa.fleet.registry.domain.TenantRepository;
import io.inoa.measurement.things.domain.Measurand;
import io.inoa.measurement.things.domain.MeasurandRepository;
import io.inoa.measurement.things.domain.MeasurandTypeRepository;
import io.inoa.measurement.things.domain.Thing;
import io.inoa.measurement.things.domain.ThingConfigurationValue;
import io.inoa.measurement.things.domain.ThingConfigurationValueRepository;
import io.inoa.measurement.things.domain.ThingRepository;
import io.inoa.measurement.things.domain.ThingType;
import io.inoa.measurement.things.domain.ThingTypeRepository;
import io.inoa.measurement.things.rest.mapper.MeasurandMapper;
import io.inoa.measurement.things.rest.mapper.ThingMapper;
import io.inoa.rest.MeasurandVO;
import io.inoa.rest.ThingCreateVO;
import io.inoa.rest.ThingUpdateVO;
import io.inoa.rest.ThingVO;
import io.inoa.rest.ThingsApi;
import io.inoa.shared.Security;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.exceptions.HttpStatusException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ThingsController implements ThingsApi {

	private final GatewayRepository gatewayRepository;
	private final TenantRepository tenantRepository;
	private final ThingRepository thingRepository;
	private final ThingTypeRepository thingTypeRepository;
	private final MeasurandRepository measurandRepository;
	private final MeasurandTypeRepository measurandTypeRepository;
	private final ThingConfigurationValueRepository thingConfigurationValueRepository;
	private final ThingMapper thingMapper;
	private final MeasurandMapper measurandMapper;
	private final Security security;

	@Override
	public HttpResponse<ThingVO> createThing(@Valid ThingCreateVO thingCreateVO) {

		var thing = thingMapper.toThing(thingCreateVO);

		// ThingID
		thing.setThingId(UUID.randomUUID());

		// Tenant
		var tenant = tenantRepository.findByTenantId(security.getTenantId());
		if (tenant.isEmpty()) {
			throw new HttpStatusException(
					HttpStatus.BAD_REQUEST, "No such tenant: " + security.getTenantId());
		}

		// Gateway
		var gateway = checkGateway(thingCreateVO.getGatewayId(), HttpStatus.BAD_REQUEST);
		thing.setGateway(gateway);

		// Name
		if (thingRepository.existsByNameAndGateway(thingCreateVO.getName(), gateway)) {
			throw new HttpStatusException(
					HttpStatus.CONFLICT, "Thing with same name already exists: " + thingCreateVO.getName());
		}

		// ThingType
		// TODO: Multiple versions not supported yet
		var thingTypes = thingTypeRepository.findByIdentifier(thingCreateVO.getThingTypeId());
		if (thingTypes.isEmpty()) {
			throw new HttpStatusException(
					HttpStatus.BAD_REQUEST, "No such thing type: " + thingCreateVO.getThingTypeId());
		}
		var thingType = thingTypes.iterator().next();
		thing.setThingType(thingTypes.iterator().next());

		// Check measurands
		if (thingCreateVO.getMeasurands() != null) {
			var invalidMeasurandTypes = thingCreateVO.getMeasurands().stream()
					.filter(
							m -> thingType.getMeasurandTypes().stream()
									.noneMatch(mt -> Objects.equals(m.getMeasurandType(), mt.getObisId())))
					.toList();
			if (!invalidMeasurandTypes.isEmpty()) {
				throw new HttpStatusException(
						HttpStatus.BAD_REQUEST,
						"Invalid measurand types not supported by given thing type: "
								+ invalidMeasurandTypes.stream().map(MeasurandVO::getMeasurandType).toList());
			}
			// Add measurands
			var measurands = thingCreateVO.getMeasurands().stream()
					.map(
							(measurandVO) -> {
								var measurand = measurandMapper.toMeasurand(measurandVO);
								measurand.setMeasurandType(
										measurandTypeRepository
												.findByObisId(measurandVO.getMeasurandType())
												.orElseThrow(
														() -> new HttpStatusException(
																HttpStatus.BAD_REQUEST,
																"Unknown measurand type: "
																		+ measurandVO.getMeasurandType())));
								return measurand;
							})
					.collect(Collectors.toSet());
			thing.setMeasurands(measurands);
		}

		if (thingCreateVO.getConfigurations() != null) {
			// Check config keys
			var invalidConfigurationKeys = thingCreateVO.getConfigurations().keySet().stream()
					.filter(
							key -> thingType.getThingConfigurations().stream()
									.noneMatch(config -> Objects.equals(key, config.getName())))
					.toList();
			if (!invalidConfigurationKeys.isEmpty()) {
				throw new HttpStatusException(
						HttpStatus.BAD_REQUEST,
						"Configuration keys that do not exist for given thing type: "
								+ invalidConfigurationKeys);
			}
			// Check config values
			var invalidConfigurationVaules = thingCreateVO.getConfigurations().entrySet().stream()
					.filter(
							entry -> thingType.getThingConfigurations().stream()
									.noneMatch(
											config -> Objects.equals(entry.getKey(), config.getName())
													&& (config.getValidationRegex() == null
															|| Pattern.compile(config.getValidationRegex())
																	.matcher(entry.getValue())
																	.matches())))
					.map(
							(entry) -> "'"
									+ entry.getValue()
									+ "' does not match regular expression: "
									+ thingType.getThingConfigurations().stream()
											.filter(config -> Objects.equals(entry.getKey(), config.getName()))
											.findFirst()
											.orElseThrow()
											.getValidationRegex())
					.toList();
			if (!invalidConfigurationVaules.isEmpty()) {
				throw new HttpStatusException(
						HttpStatus.BAD_REQUEST,
						"Some configuration values are invalid: " + invalidConfigurationVaules);
			}
			// Add configurations
			var configs = thingCreateVO.getConfigurations().entrySet().stream()
					.map(
							(configVO) -> new ThingConfigurationValue()
									.setValue(configVO.getValue())
									.setThingConfiguration(
											thingType.getThingConfigurations().stream()
													.filter(
															config -> Objects.equals(configVO.getKey(),
																	config.getName()))
													.findFirst()
													.orElseThrow(
															() -> new HttpStatusException(
																	HttpStatus.BAD_REQUEST,
																	"Unknown config key: " + configVO.getKey()))))
					.collect(Collectors.toSet());
			thing.setThingConfigurationValues(configs);
		}

		return HttpResponse.created(thingMapper.toThingVO(thingRepository.save(thing)));
	}

	@Override
	public HttpResponse<ThingVO> updateThing(UUID thingId, @Valid ThingUpdateVO thingUpdateVO) {
		var oldThing = checkThing(thingId);

		// Update name
		oldThing.setName(thingUpdateVO.getName());

		// Update description
		oldThing.setDescription(thingUpdateVO.getDescription());

		// Update gateway
		if (!Objects.equals(thingUpdateVO.getGatewayId(), oldThing.getGateway().getGatewayId())) {
			oldThing.setGateway(checkGateway(thingUpdateVO.getGatewayId(), HttpStatus.BAD_REQUEST));
		}

		// Update measurands
		List<Measurand> deletedMeasurands = new ArrayList<>();
		if (thingUpdateVO.getMeasurands() == null) {
			oldThing.setMeasurands(new HashSet<>());
		} else {
			// Removed - only keep measurands with corresponding type from update VO
			deletedMeasurands = oldThing.getMeasurands().stream()
					.filter(
							old -> thingUpdateVO.getMeasurands().stream()
									.noneMatch(
											update -> Objects.equals(
													old.getMeasurandType().getObisId(),
													update.getMeasurandType())))
					.toList();
			oldThing.setMeasurands(
					oldThing.getMeasurands().stream()
							.filter(
									old -> thingUpdateVO.getMeasurands().stream()
											.anyMatch(
													update -> Objects.equals(
															old.getMeasurandType().getObisId(),
															update.getMeasurandType())))
							.collect(Collectors.toSet()));
			// Changed - change matching measurands
			oldThing
					.getMeasurands()
					.forEach(
							(oldMeasurand) -> {
								var changedMeasurand = thingUpdateVO.getMeasurands().stream()
										.filter(
												m -> Objects.equals(
														m.getMeasurandType(),
														oldMeasurand.getMeasurandType().getObisId()))
										.findFirst()
										.orElseThrow();
								oldMeasurand.setEnabled(changedMeasurand.getEnabled());
								oldMeasurand.setTimeout(changedMeasurand.getTimeout());
								oldMeasurand.setInterval(changedMeasurand.getInterval());
							});
			// Created - add new measurands if they have no corresponding match in old thing
			oldThing
					.getMeasurands()
					.addAll(
							thingUpdateVO.getMeasurands().stream()
									.filter(
											update -> oldThing.getMeasurands().stream()
													.noneMatch(
															old -> Objects.equals(
																	old.getMeasurandType().getObisId(),
																	update.getMeasurandType())))
									.map(
											(vo) -> measurandMapper
													.toMeasurand(vo)
													.setMeasurandType(
															measurandTypeRepository
																	.findByObisId(vo.getMeasurandType())
																	.orElseThrow(
																			() -> new HttpStatusException(
																					HttpStatus.BAD_REQUEST,
																					"No such measurand type: "
																							+ vo.getMeasurandType()))))
									.collect(Collectors.toSet()));
		}

		// Update configs
		List<ThingConfigurationValue> deletedThingConfigurationValues = new ArrayList<>();
		if (thingUpdateVO.getConfigurations() == null) {
			oldThing.setThingConfigurationValues(new HashSet<>());
		} else {
			// Removed - only keep config with corresponding name from update VO
			deletedThingConfigurationValues = oldThing.getThingConfigurationValues().stream()
					.filter(
							old -> !thingUpdateVO
									.getConfigurations()
									.containsKey(old.getThingConfiguration().getName()))
					.toList();
			oldThing.setThingConfigurationValues(
					oldThing.getThingConfigurationValues().stream()
							.filter(
									old -> thingUpdateVO
											.getConfigurations()
											.containsKey(old.getThingConfiguration().getName()))
							.collect(Collectors.toSet()));
			// Changed - change matching config values
			oldThing
					.getThingConfigurationValues()
					.forEach(
							(oldConfig) -> {
								var configName = oldConfig.getThingConfiguration().getName();
								var newValue = thingUpdateVO.getConfigurations().get(configName);
								// Check value
								checkConfigValue(configName, newValue, oldThing.getThingType());
								// Update
								oldConfig.setValue(newValue);
							});
			// Created - add new configs if they have no corresponding match in old thing
			oldThing
					.getThingConfigurationValues()
					.addAll(
							thingUpdateVO.getConfigurations().entrySet().stream()
									.filter(
											update -> oldThing.getThingConfigurationValues().stream()
													.noneMatch(
															old -> Objects.equals(
																	update.getKey(),
																	old.getThingConfiguration().getName())))
									.map(
											(vo) -> new ThingConfigurationValue()
													.setValue(vo.getValue())
													.setThingConfiguration(
															oldThing.getThingType().getThingConfigurations().stream()
																	.filter(
																			config -> Objects.equals(config.getName(),
																					vo.getKey()))
																	.findFirst()
																	.orElseThrow(
																			() -> new HttpStatusException(
																					HttpStatus.BAD_REQUEST,
																					"No such config name: "
																							+ vo.getKey()))))
									.collect(Collectors.toSet()));
		}

		// Delete removed measurands and configs after all checks are done
		deletedMeasurands.forEach((measurand) -> measurandRepository.deleteById(measurand.getId()));
		deletedThingConfigurationValues.forEach(
				(thingConfigurationValue) -> thingConfigurationValueRepository
						.deleteById(thingConfigurationValue.getId()));

		return HttpResponse.ok(thingMapper.toThingVO(thingRepository.update(oldThing)));
	}

	@Override
	public HttpResponse<ThingVO> findThing(UUID thingId) {
		var thing = checkThing(thingId);
		return HttpResponse.ok(thingMapper.toThingVO(thing));
	}

	@Override
	public HttpResponse<List<ThingVO>> findThingsByGatewayId(String gatewayId) {
		var gateway = checkGateway(gatewayId);
		return HttpResponse.ok(thingMapper.toThingVOs(thingRepository.findByGateway(gateway)));
	}

	@Override
	public HttpResponse<Object> deleteThing(UUID thingId) {
		thingRepository.delete(checkThing(thingId));
		return HttpResponse.status(HttpStatus.NO_CONTENT);
	}

	private Thing checkThing(UUID thingId) {
		var thing = thingRepository.findByThingId(thingId);
		if (thing.isEmpty()
				|| !Objects.equals(
						security.getTenantId(), thing.get().getGateway().getTenant().getTenantId())) {
			throw new HttpStatusException(HttpStatus.NOT_FOUND, "No such thing.");
		}
		return thing.get();
	}

	private Gateway checkGateway(String gatewayId) {
		return checkGateway(gatewayId, HttpStatus.NOT_FOUND);
	}

	private Gateway checkGateway(String gatewayId, HttpStatus status) {
		var gateway = gatewayRepository.findByGatewayId(gatewayId);
		if (gateway.isEmpty()
				|| !Objects.equals(security.getTenantId(), gateway.get().getTenant().getTenantId())) {
			throw new HttpStatusException(status, "Gateway not found: " + gatewayId);
		}
		return gateway.get();
	}

	private void checkConfigValue(String configName, String configValue, ThingType thingType) {
		var thingConfiguration = thingType.getThingConfigurations().stream()
				.filter(config -> Objects.equals(config.getName(), configName))
				.findFirst()
				.orElseThrow(
						() -> new HttpStatusException(
								HttpStatus.BAD_REQUEST,
								"No such config '"
										+ configName
										+ "' for thing type '"
										+ thingType.getName()
										+ "'"));
		if (!Pattern.compile(thingConfiguration.getValidationRegex()).matcher(configValue).matches()) {
			throw new HttpStatusException(
					HttpStatus.BAD_REQUEST,
					"Config value '"
							+ configValue
							+ "' does not match regex '"
							+ thingConfiguration.getValidationRegex()
							+ "'");
		}
	}
}
