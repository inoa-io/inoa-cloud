package io.inoa.fleet.thing.rest;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.validation.Valid;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.inoa.fleet.thing.domain.Thing;
import io.inoa.fleet.thing.domain.ThingRepository;
import io.inoa.fleet.thing.domain.ThingType;
import io.inoa.fleet.thing.domain.ThingTypeRepository;
import io.inoa.fleet.thing.mapper.ThingMapper;
import io.inoa.fleet.thing.modbus.ConfigCreator;
import io.inoa.fleet.thing.rest.management.ThingCreateVO;
import io.inoa.fleet.thing.rest.management.ThingDetailVO;
import io.inoa.fleet.thing.rest.management.ThingPageVO;
import io.inoa.fleet.thing.rest.management.ThingUpdateVO;
import io.inoa.fleet.thing.rest.management.ThingVO;
import io.inoa.fleet.thing.rest.management.ThingsApi;
import io.micronaut.data.model.Page;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.exceptions.HttpStatusException;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ThingsController implements ThingsApi {

	/**
	 * Default sort properties, see API spec for documentation.
	 */
	public static final String SORT_ORDER_DEFAULT = ThingVO.JSON_PROPERTY_NAME;
	/**
	 * Available sort properties, see API spec for documentation.
	 */
	public static final Map<String, String> SORT_ORDER_PROPERTIES = Map.of(ThingVO.JSON_PROPERTY_NAME,
			ThingVO.JSON_PROPERTY_NAME, ThingVO.JSON_PROPERTY_CREATED, ThingVO.JSON_PROPERTY_CREATED);
	private static final String DEFAULT_TENANT = "inoa";
	private final ThingRepository thingRepository;
	private final ThingTypeRepository thingTypeRepository;
	private final ThingMapper thingMapper;
	private final PageableProvider pageableProvider;
	// private final Security security;
	private final ObjectMapper objectMapper;
	private final GatewayCommandClient gatewayCommandClient;
	private final ConfigCreatorHolder configCreatorHolder;

	@Override
	public HttpResponse<ThingVO> createThing(@Valid ThingCreateVO thingCreateVO) {
		Optional<ThingType> thingType = thingTypeRepository
				.findByThingTypeReference(thingCreateVO.getThingTypeReference());
		if (thingType.isEmpty()) {
			throw new HttpStatusException(HttpStatus.NOT_FOUND, "Thing Type not found.");
		}
		Thing thing = thingMapper.toThing(thingCreateVO);
		thing.setThingId(UUID.randomUUID().toString());
		thing.setTenantId(DEFAULT_TENANT);
		thing.setThingType(thingType.get());

		thing = thingRepository.save(thing);

		return HttpResponse.created(thingMapper.toThingVO(thing));
	}

	@Override
	public HttpResponse<ThingVO> updateThing(UUID thingId, @Valid ThingUpdateVO thingUpdateVO) {

		Optional<Thing> optionalThing = thingRepository.findByThingIdAndTenantId(thingId.toString(), DEFAULT_TENANT);
		if (optionalThing.isEmpty()) {
			throw new HttpStatusException(HttpStatus.NOT_FOUND, "Thing not found.");
		}
		var thing = optionalThing.get();
		thing.setName(thingUpdateVO.getName());
		thing.setConfig(thingUpdateVO.getConfig());
		// thing.setProperties(properties == null ? new ArrayList<>() : properties);
		thing = thingRepository.update(thing);
		return HttpResponse.ok(thingMapper.toThingVO(thing));
	}

	@Override
	public HttpResponse<ThingDetailVO> findThing(UUID thingId) {
		Optional<Thing> optionalThing = thingRepository.findByThingIdAndTenantId(thingId.toString(), DEFAULT_TENANT);
		if (optionalThing.isEmpty()) {
			throw new HttpStatusException(HttpStatus.NOT_FOUND, "Thing not found.");
		}
		var thingVO = thingMapper.toThingDetailVO(optionalThing.get());
		return HttpResponse.ok(thingVO);
	}

	// TODO filter
	@Get("/things")
	@Override
	public HttpResponse<ThingPageVO> findThings(Optional<Integer> page, Optional<Integer> size,
			Optional<List<String>> sort, Optional<String> filter) {
		var pageable = pageableProvider.getPageable(SORT_ORDER_PROPERTIES, SORT_ORDER_DEFAULT);
		Page<Thing> things = thingRepository.findByTenantId(DEFAULT_TENANT, pageable);
		return HttpResponse.ok(thingMapper.toThingPage(things));
	}

	// TODO filter
	@Get("/things/by-gateway-id/{gateway_id}")
	@Override
	public HttpResponse<ThingPageVO> findThingsByGatewayId(@PathVariable(name = "gateway_id") String gatewayId,
			Optional<Integer> page, Optional<Integer> size, Optional<List<String>> sort, Optional<String> filter) {
		var pageable = pageableProvider.getPageable(SORT_ORDER_PROPERTIES, SORT_ORDER_DEFAULT);
		Page<Thing> things = thingRepository.findByTenantIdAndGatewayId(DEFAULT_TENANT, gatewayId, pageable);
		return HttpResponse.ok(thingMapper.toThingPage(things));
	}

	@Override
	public HttpResponse<Object> syncConfigToGateway(String gatewayId) {
		ArrayNode result = generateConfigForGateway(gatewayId);
		ObjectNode objectNode = objectMapper.createObjectNode();
		objectNode.put("type", "metering.config.write");
		objectNode.set("data", result);
		gatewayCommandClient.sendGatewayCommand(DEFAULT_TENANT, gatewayId, objectNode);
		return HttpResponse.noContent();
	}

	private ArrayNode generateConfigForGateway(String gatewayId) {
		var things = thingRepository.findAllByTenantIdAndGatewayId(DEFAULT_TENANT, gatewayId);
		ArrayNode result = objectMapper.createArrayNode();
		for (var thing : things) {
			ThingType thingType = thing.getThingType();
			ConfigCreator configCreator = configCreatorHolder.getConfigCreator(thingType);
			JsonNode nodes = configCreator.build(thing, thingType);
			for (var node : nodes) {
				result.add(node);
			}
		}
		return result;
	}

	@Override
	public HttpResponse<Object> deleteThing(UUID thingId) {
		Optional<Thing> optionalThing = thingRepository.findByThingIdAndTenantId(thingId.toString(), DEFAULT_TENANT);
		if (optionalThing.isEmpty()) {
			throw new HttpStatusException(HttpStatus.NOT_FOUND, "Thing not found.");
		}
		thingRepository.delete(optionalThing.get());
		return HttpResponse.noContent();
	}

	@Override
	public HttpResponse<Object> downloadConfigToGateway(String gatewayId) {
		ArrayNode result = generateConfigForGateway(gatewayId);
		return HttpResponse.ok(result);
	}
}
