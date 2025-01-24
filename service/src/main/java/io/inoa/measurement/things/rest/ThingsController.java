package io.inoa.measurement.things.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.inoa.measurement.things.builder.ConfigCreatorHolder;
import io.inoa.rest.RemoteApi;
import io.inoa.rest.ThingCreateVO;
import io.inoa.rest.ThingPageVO;
import io.inoa.rest.ThingUpdateVO;
import io.inoa.rest.ThingVO;
import io.inoa.rest.ThingsApi;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.http.HttpResponse;
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

  private static final String DEFAULT_TENANT = "inoa";

  private final ObjectMapper objectMapper;
  private final RemoteApi remoteApiClient;
  private final ConfigCreatorHolder configCreatorHolder;

  @Override
  public HttpResponse<ThingVO> createThing(@Valid ThingCreateVO thingCreateVO) {
    return HttpResponse.status(500, "Not implemented yet");
  }

  @Override
  public HttpResponse<ThingVO> updateThing(UUID thingId, @Valid ThingUpdateVO thingUpdateVO) {
    return HttpResponse.status(500, "Not implemented yet");
  }

  @Override
  public HttpResponse<ThingVO> findThing(UUID thingId) {
    return HttpResponse.status(500, "Not implemented yet");
  }

  // TODO filter
  @Override
  public HttpResponse<ThingPageVO> findThings(
      Optional<Integer> page,
      Optional<Integer> size,
      Optional<List<String>> sort,
      Optional<String> filter) {
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
    return HttpResponse.status(500, "Not implemented yet");
  }

  @Override
  public HttpResponse<Object> syncConfigToGateway(@NonNull String gatewayId) {
    return HttpResponse.status(500, "Not implemented yet");
  }

  @Override
  public HttpResponse<Object> syncConfigToGatewaySequential(@NonNull String gatewayId) {
    return HttpResponse.status(500, "Not implemented yet");
  }

  @Override
  public HttpResponse<Object> deleteThing(UUID thingId) {
    return HttpResponse.status(500, "Not implemented yet");
  }

  @Override
  public HttpResponse<Object> downloadConfigToGatewayLegacy(String gatewayId) {
    return HttpResponse.status(500, "Not implemented yet");
  }

  @Override
  public HttpResponse<Object> downloadConfigToGateway(String gatewayId) {
    return HttpResponse.status(500, "Not implemented yet");
  }
}
