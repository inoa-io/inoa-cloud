package io.inoa.measurement.things.rest;

import io.inoa.measurement.things.domain.ThingTypeRepository;
import io.inoa.measurement.things.rest.mapper.ThingTypeMapper;
import io.inoa.rest.ThingTypeUpdateVO;
import io.inoa.rest.ThingTypeVO;
import io.inoa.rest.ThingTypesApi;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import jakarta.inject.Inject;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ThingTypesController implements ThingTypesApi {

  private final ThingTypeMapper mapper;

  @Inject private ThingTypeRepository thingTypeRepository;

  @Override
  public HttpResponse<ThingTypeVO> createThingType(ThingTypeVO thingTypeVO) {
    return HttpResponse.status(500, "Not implemented yet");
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
