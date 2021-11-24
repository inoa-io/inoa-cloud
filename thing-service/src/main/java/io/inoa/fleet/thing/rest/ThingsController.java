package io.inoa.fleet.thing.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.validation.Valid;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import io.inoa.fleet.thing.CRC16;
import io.inoa.fleet.thing.InoaSateliteModbusRTUBuilder;
import io.inoa.fleet.thing.domain.Thing;
import io.inoa.fleet.thing.domain.ThingChannel;
import io.inoa.fleet.thing.domain.ThingChannelRepository;
import io.inoa.fleet.thing.domain.ThingRepository;
import io.inoa.fleet.thing.domain.ThingType;
import io.inoa.fleet.thing.domain.ThingTypeChannel;
import io.inoa.fleet.thing.domain.ThingTypeChannelRepository;
import io.inoa.fleet.thing.domain.ThingTypeRepository;
import io.inoa.fleet.thing.mapper.ThingMapper;
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

	private final ThingRepository thingRepository;
	private final ThingChannelRepository thingChannelRepository;
	private final ThingTypeRepository thingTypeRepository;
	private final ThingTypeChannelRepository thingTypeChannelRepository;
	private final ThingMapper thingMapper;
	private final PageableProvider pageableProvider;
	private final Security security;
	private final ObjectMapper objectMapper;

	@Override
	public HttpResponse<ThingVO> createThing(@Valid ThingCreateVO thingCreateVO) {
		Optional<ThingType> thingType = thingTypeRepository.findByThingTypeId(thingCreateVO.getThingTypeId());
		if (thingType.isEmpty()) {
			throw new HttpStatusException(HttpStatus.NOT_FOUND, "Thing Type not found.");
		}
		Thing thing = thingMapper.toThing(thingCreateVO);
		thing.setThingId(UUID.randomUUID());
		thing.setTenantId(security.getTenantId());
		thing.setThingType(thingType.get());
		if (thing.getProperties() == null) {
			thing.setProperties(new ArrayList<>());
		}
		thing = thingRepository.save(thing);
		if (thingCreateVO.getChannels() != null) {
			for (var channel : thingCreateVO.getChannels()) {
				thingChannelRepository.save(new ThingChannel().setName(channel.getName()).setThing(thing)
						.setThingChannelId(UUID.randomUUID())
						.setProperties(thingMapper.toPropertyList(channel.properties())));
			}
		}
		return HttpResponse.created(thingMapper.toThingVO(thing));
	}

	@Override
	public HttpResponse<ThingVO> updateThing(UUID thingId, @Valid ThingUpdateVO thingUpdateVO) {

		Optional<Thing> optionalThing = thingRepository.findByThingIdAndTenantId(thingId, security.getTenantId());
		if (optionalThing.isEmpty()) {
			throw new HttpStatusException(HttpStatus.NOT_FOUND, "Thing not found.");
		}
		var thing = optionalThing.get();
		thing.setName(thingUpdateVO.getName());
		var properties = thingMapper.toPropertyList(thingUpdateVO.getProperties());
		thing.setProperties(properties == null ? new ArrayList<>() : properties);
		thing = thingRepository.update(thing);
		return HttpResponse.ok(thingMapper.toThingVO(thing));
	}

	@Override
	public HttpResponse<ThingDetailVO> findThing(UUID thingId) {
		Optional<Thing> optionalThing = thingRepository.findByThingIdAndTenantId(thingId, security.getTenantId());
		if (optionalThing.isEmpty()) {
			throw new HttpStatusException(HttpStatus.NOT_FOUND, "Thing not found.");
		}
		List<ThingChannel> channels = thingChannelRepository.findByThing(optionalThing.get());
		var thingVO = thingMapper.toThingDetailVO(optionalThing.get());
		thingVO.setChannels(thingMapper.toThingChannelVOList(channels));
		return HttpResponse.ok(thingVO);
	}

	// TODO filter
	@Get("/things")
	@Override
	public HttpResponse<ThingPageVO> findThings(Optional<Integer> page, Optional<Integer> size,
			Optional<List<String>> sort, Optional<String> filter) {
		var pageable = pageableProvider.getPageable(SORT_ORDER_PROPERTIES, SORT_ORDER_DEFAULT);
		Page<Thing> things = thingRepository.findByTenantId(security.getTenantId(), pageable);
		return HttpResponse.ok(thingMapper.toThingPage(things));
	}

	// TODO filter
	@Get("/things/byGatewayId/{gateway_id}")
	@Override
	public HttpResponse<ThingPageVO> findThingsByGatewayId(@PathVariable(name = "gateway_id") UUID gatewayId,
			Optional<Integer> page, Optional<Integer> size, Optional<List<String>> sort, Optional<String> filter) {
		var pageable = pageableProvider.getPageable(SORT_ORDER_PROPERTIES, SORT_ORDER_DEFAULT);
		Page<Thing> things = thingRepository.findByTenantIdAndGatewayId(security.getTenantId(), gatewayId, pageable);
		return HttpResponse.ok(thingMapper.toThingPage(things));
	}

	@Override
	public HttpResponse<Object> syncConfigToGateway(UUID gatewayId) {
		var things = thingRepository.findAllByTenantIdAndGatewayId(security.getTenantId(), gatewayId);
		ArrayNode result = objectMapper.createArrayNode();
		InoaSateliteModbusRTUBuilder builder = new InoaSateliteModbusRTUBuilder(objectMapper, new CRC16());
		for (var thing : things) {
			List<ThingTypeChannel> thingTypeChannels = thingTypeChannelRepository.findByThingType(thing.getThingType());
			List<ThingChannel> channels = thingChannelRepository.findByThing(thing);
			JsonNode nodes = builder.build(thing, thingTypeChannels, channels);
			for (var node : nodes) {
				result.add(node);
			}
		}
		return HttpResponse.ok(result);
	}

	@Override
	public HttpResponse<Object> deleteThing(UUID thingId) {
		Optional<Thing> optionalThing = thingRepository.findByThingIdAndTenantId(thingId, security.getTenantId());
		if (optionalThing.isEmpty()) {
			throw new HttpStatusException(HttpStatus.NOT_FOUND, "Thing not found.");
		}
		thingRepository.delete(optionalThing.get());
		return HttpResponse.noContent();
	}
}
