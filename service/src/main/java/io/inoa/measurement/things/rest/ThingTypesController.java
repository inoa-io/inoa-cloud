package io.inoa.measurement.things.rest;

import io.inoa.measurement.things.domain.MeasurandTypeRepository;
import io.inoa.measurement.things.domain.ThingType;
import io.inoa.measurement.things.domain.ThingTypeRepository;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.http.annotation.Controller;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ThingTypesController {

  @Inject private MeasurandTypeRepository measurandTypeRepository;
  @Inject private ThingTypeRepository thingTypeRepository;

  public @NonNull ThingType createThingType() {
    return null;
  }
}
