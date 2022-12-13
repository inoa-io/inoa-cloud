package io.inoa.fleet.thing.rest;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.validation.Valid;

import io.inoa.fleet.thing.domain.ThingType;
import io.inoa.fleet.thing.domain.ThingTypeRepository;
import io.inoa.fleet.thing.mapper.ThingTypeMapper;
import io.inoa.fleet.thing.rest.management.ThingTypeCreateVO;
import io.inoa.fleet.thing.rest.management.ThingTypeDetailVO;
import io.inoa.fleet.thing.rest.management.ThingTypePageVO;
import io.inoa.fleet.thing.rest.management.ThingTypeUpdateVO;
import io.inoa.fleet.thing.rest.management.ThingTypeVO;
import io.inoa.fleet.thing.rest.management.ThingTypesApi;
import io.micronaut.data.model.Page;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.exceptions.HttpStatusException;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ThingTypesController implements ThingTypesApi {

	/** Default sort properties, see API spec for documentation. */
	public static final String SORT_ORDER_DEFAULT = ThingTypeVO.JSON_PROPERTY_NAME;
	/** Available sort properties, see API spec for documentation. */
	public static final Map<String, String> SORT_ORDER_PROPERTIES = Map.of(ThingTypeVO.JSON_PROPERTY_NAME,
			ThingTypeVO.JSON_PROPERTY_NAME, ThingTypeVO.JSON_PROPERTY_CREATED, ThingTypeVO.JSON_PROPERTY_CREATED);

	private final ThingTypeRepository thingTypeRepository;
	private final ThingTypeMapper thingTypeMapper;
	private final PageableProvider pageableProvider;

	@Override
	public HttpResponse<ThingTypeVO> createThingType(@Valid ThingTypeCreateVO thingTypeCreateVO) {
		ThingType thingType = thingTypeMapper.toThingType(thingTypeCreateVO);
		thingType.setThingTypeId(UUID.randomUUID().toString());

		thingType = thingTypeRepository.save(thingType);

		return HttpResponse.created(thingTypeMapper.toThingTypeVO(thingType));
	}

	@Override
	public HttpResponse<ThingTypeVO> createThingTypeWithId(String thingTypeId, ThingTypeCreateVO thingTypeCreateVO) {
		ThingType thingType = thingTypeMapper.toThingType(thingTypeCreateVO);
		thingType.setThingTypeId(thingTypeId);
		thingType = thingTypeRepository.save(thingType);
		return HttpResponse.created(thingTypeMapper.toThingTypeVO(thingType));
	}

	@Override
	public HttpResponse<Object> deleteThingType(String thingTypeId) {
		Optional<ThingType> optionalThingType = thingTypeRepository.findByThingTypeId(thingTypeId);
		if (optionalThingType.isEmpty()) {
			throw new HttpStatusException(HttpStatus.NOT_FOUND, "Thing Type not found.");
		}
		thingTypeRepository.delete(optionalThingType.get());
		return HttpResponse.noContent();
	}

	@Override
	public HttpResponse<ThingTypeVO> findThingType(String thingTypeId) {
		Optional<ThingType> optionalThingType = thingTypeRepository.findByThingTypeId(thingTypeId);
		if (optionalThingType.isEmpty()) {
			throw new HttpStatusException(HttpStatus.NOT_FOUND, "Thing Type not found.");
		}
		return HttpResponse.ok(thingTypeMapper.toThingTypeVO(optionalThingType.get()));
	}

	@Override
	public HttpResponse<ThingTypeVO> findThingTypeByReference(String thingTypeReference) {
		Optional<ThingType> optionalThingType = thingTypeRepository.findByThingTypeReference(thingTypeReference);
		if (optionalThingType.isEmpty()) {
			throw new HttpStatusException(HttpStatus.NOT_FOUND, "Thing Type not found.");
		}
		return HttpResponse.ok(thingTypeMapper.toThingTypeVO(optionalThingType.get()));
	}

	@Override
	public HttpResponse<ThingTypeDetailVO> findThingTypeWithDetails(String thingTypeId) {
		Optional<ThingType> optionalThingType = thingTypeRepository.findByThingTypeId(thingTypeId);
		if (optionalThingType.isEmpty()) {
			throw new HttpStatusException(HttpStatus.NOT_FOUND, "Thing Type not found.");
		}
		var thingTypeVO = thingTypeMapper.toThingTypeDetailVO(optionalThingType.get());
		return HttpResponse.ok(thingTypeVO);
	}

	@Get("/thing-types")
	@Override
	public HttpResponse<ThingTypePageVO> findThingTypes(Optional<Integer> page, Optional<Integer> size,
			Optional<List<String>> sort, Optional<String> optionalFilter) {
		var pageable = pageableProvider.getPageable(SORT_ORDER_PROPERTIES, SORT_ORDER_DEFAULT);
		Page<ThingType> thingTypes = optionalFilter.map(filter -> "%" + filter.replace("*", "%") + "%")
				.map(filter -> thingTypeRepository.findByNameIlike(filter, pageable))
				.orElseGet(() -> thingTypeRepository.findAll(pageable));
		return HttpResponse.ok(thingTypeMapper.toThingTypePage(thingTypes));
	}

	@Override
	public HttpResponse<ThingTypeVO> updateThingType(String thingTypeId, @Valid ThingTypeUpdateVO thingTypeUpdateVO) {

		Optional<ThingType> optionalThingType = thingTypeRepository.findByThingTypeId(thingTypeId);
		if (optionalThingType.isEmpty()) {
			throw new HttpStatusException(HttpStatus.NOT_FOUND, "Thing Type not found.");
		}
		var thingType = optionalThingType.get();
		thingType.setName(thingTypeUpdateVO.getName());
		thingType = thingTypeRepository.update(thingType);
		return HttpResponse.ok(thingTypeMapper.toThingTypeVO(thingType));
	}
}
