package io.inoa.measurement.things.rest;

import io.inoa.fleet.registry.domain.GatewayRepository;
import io.inoa.fleet.registry.domain.TenantRepository;
import io.inoa.measurement.things.domain.ThingRepository;
import io.inoa.measurement.things.domain.ThingTypeRepository;
import io.inoa.measurement.things.rest.mapper.ThingMapper;
import io.inoa.rest.ThingCreateVO;
import io.inoa.rest.ThingPageVO;
import io.inoa.rest.ThingUpdateVO;
import io.inoa.rest.ThingVO;
import io.inoa.rest.ThingsApi;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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
  private final ThingMapper mapper;
  private final Security security;

  @Override
  public HttpResponse<ThingVO> createThing(@Valid ThingCreateVO thingCreateVO) {

    var thing = mapper.toThing(thingCreateVO);

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
    var thingType = thingTypeRepository.findByIdentifier(thingCreateVO.getThingTypeId());
    if (thingType.isEmpty()) {
      return HttpResponse.status(
          HttpStatus.BAD_REQUEST, "No such thing type: " + thingCreateVO.getThingTypeId());
    }
    thing.setThingType(thingType.iterator().next());

    return HttpResponse.created(mapper.toThingVO(thingRepository.save(thing)));
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
    return HttpResponse.ok(mapper.toThingVO(thing.get()));
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
