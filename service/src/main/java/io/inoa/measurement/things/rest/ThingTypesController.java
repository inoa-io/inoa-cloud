package io.inoa.measurement.things.rest;

import io.inoa.measurement.things.domain.MeasurandType;
import io.inoa.measurement.things.domain.MeasurandTypeRepository;
import io.inoa.measurement.things.domain.ThingTypeRepository;
import io.inoa.measurement.things.rest.mapper.ThingTypeMapper;
import io.inoa.rest.ThingTypeCreateVO;
import io.inoa.rest.ThingTypeUpdateVO;
import io.inoa.rest.ThingTypeVO;
import io.inoa.rest.ThingTypesApi;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ThingTypesController implements ThingTypesApi {

  private final ThingTypeMapper mapper;

  @Inject private ThingTypeRepository thingTypeRepository;
  @Inject private MeasurandTypeRepository measurandTypeRepository;

  @Override
  public HttpResponse<ThingTypeVO> createThingType(ThingTypeCreateVO thingTypeCreateVO) {
    // Check Measurands
    var measurands = new ArrayList<MeasurandType>();
    for (var measurand : thingTypeCreateVO.getMeasurands()) {
      var measurandType = measurandTypeRepository.findByObisId(measurand);
      if (measurandType == null) {
        return HttpResponse.status(
            HttpStatus.BAD_REQUEST, "Measurand '" + measurand + "'not found");
      }
      measurands.add(measurandType);
    }

    var thingType = mapper.toThingType(thingTypeCreateVO);
    thingType.setMeasurandTypes(measurands);
    // thingType.getThingConfigurations().forEach(thingConfiguration ->
    // {thingConfiguration.setThingType(thingType);});

    thingTypeRepository.save(thingType);

    return findThingType(thingTypeCreateVO.getIdentifier());
  }

  @Override
  public HttpResponse<Object> deleteThingType(String thingTypeId) {
    return HttpResponse.status(500, "Not implemented yet");
  }

  @Override
  public HttpResponse<ThingTypeVO> findThingType(String thingTypeId) {
    var result = thingTypeRepository.findByIdentifier(thingTypeId);
    if (result == null || result.isEmpty()) {
      return HttpResponse.notFound();
    }
    // TODO: Multiple thing type versions not supported yet
    return HttpResponse.ok(mapper.toThingTypeVO(result.iterator().next()));
  }

  @Override
  public HttpResponse<List<ThingTypeVO>> getThingTypes() {
    return HttpResponse.status(500, "Not implemented yet");
  }

  @Override
  public HttpResponse<ThingTypeVO> updateThingType(
      String thingTypeId, ThingTypeUpdateVO thingTypeUpdateVO) {
    return HttpResponse.status(500, "Not implemented yet");
  }
}
