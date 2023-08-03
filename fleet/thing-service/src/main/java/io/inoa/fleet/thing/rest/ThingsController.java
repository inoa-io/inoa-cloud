package io.inoa.fleet.thing.rest;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.validation.Valid;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.inoa.fleet.thing.domain.Thing;
import io.inoa.fleet.thing.domain.ThingRepository;
import io.inoa.fleet.thing.domain.ThingType;
import io.inoa.fleet.thing.domain.ThingTypeRepository;
import io.inoa.fleet.thing.driver.ConfigCreator;
import io.inoa.fleet.thing.driver.ConfigCreatorHolder;
import io.inoa.fleet.thing.mapper.ThingMapper;
import io.inoa.fleet.thing.rest.management.ThingCreateVO;
import io.inoa.fleet.thing.rest.management.ThingDetailVO;
import io.inoa.fleet.thing.rest.management.ThingPageVO;
import io.inoa.fleet.thing.rest.management.ThingUpdateVO;
import io.inoa.fleet.thing.rest.management.ThingVO;
import io.inoa.fleet.thing.rest.management.ThingsApi;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.model.Page;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.exceptions.HttpStatusException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
	public HttpResponse<Object> syncConfigToGateway(@NonNull String gatewayId) {
		ArrayNode result = generateConfigForGatewayLegacy(gatewayId);
		ObjectNode objectNode = objectMapper.createObjectNode();
		objectNode.put("id", "1");
		objectNode.put("method", "dp.write");
		objectNode.set("params", result);
		try {
			int length = objectMapper.writeValueAsBytes(objectNode).length;
			if (length > 70656) {
				return HttpResponse.badRequest(String.format("rcp call to big %d", length));
			}
		} catch (JsonProcessingException e) {
			return HttpResponse.badRequest();
		}
		gatewayCommandClient.sendGatewayCommand(DEFAULT_TENANT, gatewayId, objectNode);
		return HttpResponse.noContent();
	}

	@Override
	public HttpResponse<Object> syncConfigToGatewaySequential(@NonNull String gatewayId) {
		// Clear all datapoints via RPC
		ObjectNode datapointClearCommand = objectMapper.createObjectNode();
		datapointClearCommand.put("id", "1");
		datapointClearCommand.put("method", "dp.clear");
		gatewayCommandClient.sendGatewayCommand(DEFAULT_TENANT, gatewayId, datapointClearCommand);
		// Sequentially send datapoints via RPC
		ArrayNode datapointsForThisThing = generateConfigForGateway(gatewayId);
		for (var node : datapointsForThisThing) {
			ObjectNode datapointAddCommand = objectMapper.createObjectNode();
			datapointAddCommand.put("id", "1");
			datapointAddCommand.put("method", "dp.add");
			datapointAddCommand.set("params", node);
			gatewayCommandClient.sendGatewayCommand(DEFAULT_TENANT, gatewayId, datapointAddCommand);
		}
		return HttpResponse.noContent();
	}

	private ArrayNode generateConfigForGatewayLegacy(String gatewayId) {
		var things = thingRepository.findAllByTenantIdAndGatewayId(DEFAULT_TENANT, gatewayId);
		ArrayNode result = objectMapper.createArrayNode();
		for (var thing : things) {
			ThingType thingType = thing.getThingType();
			Optional<ConfigCreator> configCreator = configCreatorHolder.getConfigCreator(thingType);
			if (configCreator.isPresent()) {
				JsonNode nodes = configCreator.get().buildLegacy(thing, thingType);
				for (var node : nodes) {
					result.add(node);
				}
			} else {
				log.warn("no config creator found for thing type: {}", thingType.getThingTypeReference());
			}
		}
		return result;
	}

	private ArrayNode generateConfigForGateway(String gatewayId) {
		var things = thingRepository.findAllByTenantIdAndGatewayId(DEFAULT_TENANT, gatewayId);
		ArrayNode result = objectMapper.createArrayNode();
		for (var thing : things) {
			ThingType thingType = thing.getThingType();
			Optional<ConfigCreator> configCreator = configCreatorHolder.getConfigCreator(thingType);
			if (configCreator.isPresent()) {
				JsonNode nodes = null;
				try {
					nodes = configCreator.get().build(thing, thingType);
				} catch (JsonProcessingException e) {
					log.error("No appropriate JSON could be generated for thing type: {}",
						thingType.getThingTypeReference());
					continue;
				}
				for (var node : nodes) {
					result.add(node);
				}
			} else {
				log.warn("No config creator found for thing type: {}", thingType.getThingTypeReference());
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
	public HttpResponse<Object> downloadConfigToGatewayLegacy(String gatewayId) {
		ArrayNode result = generateConfigForGatewayLegacy(gatewayId);
		return HttpResponse.ok(result);
	}

	@Override
	public HttpResponse<Object> downloadConfigToGateway(String gatewayId) {
		ArrayNode result = generateConfigForGateway(gatewayId);
		return HttpResponse.ok(result);
	}
}
