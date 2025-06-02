package io.inoa.measurement.things.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.inject.Inject;

import io.inoa.measurement.things.domain.MeasurandType;
import io.inoa.measurement.things.domain.MeasurandTypeRepository;
import io.inoa.measurement.things.domain.ThingTypeRepository;
import io.inoa.measurement.things.rest.mapper.MeasurandTypeMapper;
import io.inoa.measurement.things.rest.mapper.ThingTypeMapper;
import io.inoa.rest.MeasurandTypeVO;
import io.inoa.rest.ThingTypeCreateVO;
import io.inoa.rest.ThingTypeUpdateVO;
import io.inoa.rest.ThingTypeVO;
import io.inoa.rest.ThingTypesApi;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.exceptions.HttpStatusException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ThingTypesController implements ThingTypesApi {

	private final ThingTypeMapper thingTypeMapper;
	private final MeasurandTypeMapper measurandTypeMapper;

	@Inject
	private ThingTypeRepository thingTypeRepository;
	@Inject
	private MeasurandTypeRepository measurandTypeRepository;

	@Override
	public HttpResponse<ThingTypeVO> createThingType(ThingTypeCreateVO thingTypeCreateVO) {
		// Check Duplicates
		if (!thingTypeRepository.findByIdentifier(thingTypeCreateVO.getIdentifier()).isEmpty()) {
			return HttpResponse.status(HttpStatus.CONFLICT);
		}

		// Check Measurands
		var measurands = new ArrayList<MeasurandType>();
		for (var measurand : thingTypeCreateVO.getMeasurands()) {
			var measurandType = measurandTypeRepository
					.findByObisId(measurand)
					.orElseThrow(
							() -> new HttpStatusException(
									HttpStatus.BAD_REQUEST, "Unknown measurand type: " + measurand));
			if (measurandType == null) {
				return HttpResponse.status(
						HttpStatus.BAD_REQUEST, "Measurand '" + measurand + "'not found");
			}
			measurands.add(measurandType);
		}

		var thingType = thingTypeMapper.toThingType(thingTypeCreateVO);
		thingType.setMeasurandTypes(measurands);

		return HttpResponse.created(thingTypeMapper.toThingTypeVO(thingTypeRepository.save(thingType)));
	}

	@Override
	public HttpResponse<Object> deleteThingType(String thingTypeId) {
		// Check existing
		if (thingTypeRepository.findByIdentifier(thingTypeId).isEmpty()) {
			return HttpResponse.status(HttpStatus.NOT_FOUND);
		}

		thingTypeRepository.deleteByIdentifier(thingTypeId);
		return HttpResponse.status(HttpStatus.NO_CONTENT);
	}

	@Override
	public HttpResponse<ThingTypeVO> findThingType(String thingTypeId) {
		var result = thingTypeRepository.findByIdentifier(thingTypeId);
		if (result == null || result.isEmpty()) {
			return HttpResponse.notFound();
		}
		// TODO: Multiple thing type versions not supported yet
		return HttpResponse.ok(thingTypeMapper.toThingTypeVO(result.iterator().next()));
	}

	@Override
	public HttpResponse<List<ThingTypeVO>> getThingTypes() {
		return HttpResponse.ok(
				thingTypeRepository.findAll().stream()
						.map(thingTypeMapper::toThingTypeVO)
						.collect(Collectors.toList()));
	}

	@Override
	public HttpResponse<ThingTypeVO> updateThingType(
			String thingTypeId, ThingTypeUpdateVO thingTypeUpdateVO) {
		// Check existing
		var thingTypes = thingTypeRepository.findByIdentifier(thingTypeId);
		if (thingTypes.isEmpty()) {
			return HttpResponse.status(HttpStatus.NOT_FOUND);
		}
		// TODO: No tenant in scope here yet
		var thingType = thingTypes.iterator().next();
		var thingTypeUpdate = thingTypeMapper.toThingType(thingTypeUpdateVO);

		thingType.setName(thingTypeUpdate.getName());
		thingType.setDescription(thingTypeUpdate.getDescription());
		thingType.setCategory(thingTypeUpdate.getCategory());
		thingType.setProtocol(thingTypeUpdate.getProtocol());
		thingType.setVersion(thingTypeUpdate.getVersion());
		thingType.setMeasurandTypes(thingTypeUpdate.getMeasurandTypes());
		thingType.setThingConfigurations(thingTypeUpdate.getThingConfigurations());

		return HttpResponse.ok(thingTypeMapper.toThingTypeVO(thingTypeRepository.update(thingType)));
	}

	@Override
	public HttpResponse<List<MeasurandTypeVO>> findMeasurandTypes() {
		return HttpResponse.ok(
				measurandTypeRepository.findAll().stream()
						.map(measurandTypeMapper::toMeasurandTypeVO)
						.toList());
	}
}
