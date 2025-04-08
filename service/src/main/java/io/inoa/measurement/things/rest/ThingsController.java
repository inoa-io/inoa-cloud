package io.inoa.measurement.things.rest;

import io.inoa.fleet.registry.domain.GatewayRepository;
import io.inoa.fleet.registry.domain.TenantRepository;
import io.inoa.measurement.things.domain.MeasurandTypeRepository;
import io.inoa.measurement.things.domain.ThingRepository;
import io.inoa.measurement.things.domain.ThingTypeRepository;
import io.inoa.measurement.things.rest.mapper.MeasurandMapper;
import io.inoa.measurement.things.rest.mapper.ThingMapper;
import io.inoa.rest.MeasurandVO;
import io.inoa.rest.ThingCreateVO;
import io.inoa.rest.ThingPageVO;
import io.inoa.rest.ThingUpdateVO;
import io.inoa.rest.ThingVO;
import io.inoa.rest.ThingsApi;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.exceptions.HttpStatusException;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
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
  private final MeasurandTypeRepository measurandTypeRepository;
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
      return HttpResponse.status(
          HttpStatus.BAD_REQUEST, "No such tenant: " + security.getTenantId());
    }
    thing.setTenant(tenant.get());

    // Gateway
    // TODO: Check if Gateway is visible to user! Otherwise send 404
    var gateway = gatewayRepository.findByGatewayId(thingCreateVO.getGatewayId());
    if (gateway.isEmpty()) {
      return HttpResponse.status(
          HttpStatus.BAD_REQUEST, "No such gateway: " + thingCreateVO.getGatewayId());
    }
    thing.setGateway(gateway.get());

    // ThingType
    // TODO: Multiple versions not supported yet
    var thingTypes = thingTypeRepository.findByIdentifier(thingCreateVO.getThingTypeId());
    if (thingTypes.isEmpty()) {
      return HttpResponse.status(
          HttpStatus.BAD_REQUEST, "No such thing type: " + thingCreateVO.getThingTypeId());
    }
    var thingType = thingTypes.iterator().next();
    thing.setThingType(thingTypes.iterator().next());

    // Check measurands
    var invalidMeasurandTypes =
        thingCreateVO.getMeasurands().stream()
            .filter(
                m ->
                    thingType.getMeasurandTypes().stream()
                        .noneMatch(mt -> Objects.equals(m.getMeasurandType(), mt.getObisId())))
            .toList();
    if (!invalidMeasurandTypes.isEmpty()) {
      return HttpResponse.status(
          HttpStatus.BAD_REQUEST,
          "Invalid measurand types not supported by given thing type: "
              + invalidMeasurandTypes.stream().map(MeasurandVO::getMeasurandType).toList());
    }

    // Check config keys
    var invalidConfigurationKeys =
        thingCreateVO.getConfigurations().keySet().stream()
            .filter(
                key ->
                    thingType.getThingConfigurations().stream()
                        .noneMatch(config -> Objects.equals(key, config.getName())))
            .toList();
    if (!invalidConfigurationKeys.isEmpty()) {
      return HttpResponse.status(
          HttpStatus.BAD_REQUEST,
          "Configuration keys that do not exist for given thing type: " + invalidConfigurationKeys);
    }

    // Check config values
    var invalidConfigurationVaules =
        thingCreateVO.getConfigurations().entrySet().stream()
            .filter(
                entry ->
                    thingType.getThingConfigurations().stream()
                        .noneMatch(
                            config ->
                                Objects.equals(entry.getKey(), config.getName())
                                    && Pattern.compile(config.getValidationRegex())
                                        .matcher(entry.getValue())
                                        .matches()))
            .map(
                (entry) ->
                    "'"
                        + entry.getValue()
                        + "' does not match regular expression: "
                        + thingType.getThingConfigurations().stream()
                            .filter(config -> Objects.equals(entry.getKey(), config.getName()))
                            .findFirst()
                            .orElseThrow()
                            .getName())
            .toList();
    if (!invalidConfigurationVaules.isEmpty()) {
      return HttpResponse.status(
          HttpStatus.BAD_REQUEST,
          "Some configuration values are invalid: " + invalidConfigurationVaules);
    }

    // Add measurands
    var measurands =
        thingCreateVO.getMeasurands().stream()
            .map(
                (measurandVO) -> {
                  var measurand = measurandMapper.toMeasurand(measurandVO);
                  measurand.setThing(thing);
                  measurand.setMeasurandType(
                      measurandTypeRepository
                          .findByObisId(measurandVO.getMeasurandType())
                          .orElseThrow(
                              () ->
                                  new HttpStatusException(
                                      HttpStatus.BAD_REQUEST,
                                      "Unknown measurand type: "
                                          + measurandVO.getMeasurandType())));
                  return measurand;
                })
            .collect(Collectors.toSet());
    thing.setMeasurands(measurands);

    // TODO: Add configs

    return HttpResponse.created(thingMapper.toThingVO(thingRepository.save(thing)));
  }

  @Override
  public HttpResponse<ThingVO> updateThing(UUID thingId, @Valid ThingUpdateVO thingUpdateVO) {
    // TODO: Check if Gateway is visible to user! Otherwise send 404
    return HttpResponse.status(500, "Not implemented yet");
  }

  @Override
  public HttpResponse<ThingVO> findThing(UUID thingId) {
    // TODO: Check if Gateway is visible to user! Otherwise send 404
    var thing = thingRepository.findByThingId(thingId);
    if (thing.isEmpty()) {
      return HttpResponse.status(HttpStatus.NOT_FOUND);
    }
    return HttpResponse.ok(thingMapper.toThingVO(thing.get()));
  }

  // TODO filter
  @Override
  public HttpResponse<ThingPageVO> findThings(
      Optional<Integer> page,
      Optional<Integer> size,
      Optional<List<String>> sort,
      Optional<String> filter) {
    // TODO: Check if Gateway is visible to user! Otherwise send 404
    return HttpResponse.status(500, "Not implemented yet");
  }

  // TODO filter
  @Override
  public HttpResponse<ThingPageVO> findThingsByGatewayId(
      String gatewayId,
      Optional<Integer> page,
      Optional<Integer> size,
      Optional<List<String>> sort,
      Optional<String> nameFilter,
      Optional<String> referenceFilter) {
    // TODO: Check if Gateway is visible to user! Otherwise send 404
    return HttpResponse.status(500, "Not implemented yet");
  }

  @Override
  public HttpResponse<Object> syncThingsToGateway(String gatewayId) {
    // TODO: Check if Gateway is visible to user! Otherwise send 404
    return HttpResponse.status(500, "Not implemented yet");
  }

  @Override
  public HttpResponse<Object> deleteThing(UUID thingId) {
    // TODO: Check if Gateway is visible to user! Otherwise send 404
    var thing = thingRepository.findByThingId(thingId);
    if (thing.isEmpty()) {
      return HttpResponse.status(HttpStatus.NOT_FOUND);
    }
    thingRepository.delete(thing.get());
    return HttpResponse.status(HttpStatus.NO_CONTENT);
  }
}
