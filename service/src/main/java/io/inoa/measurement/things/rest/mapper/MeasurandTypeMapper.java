package io.inoa.measurement.things.rest.mapper;

import io.inoa.measurement.things.domain.MeasurandType;
import io.inoa.rest.MeasurandTypeVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.JAKARTA)
public interface MeasurandTypeMapper {

  MeasurandTypeVO toMeasurandTypeVO(MeasurandType measurandType);

  @Mapping(target = "id", ignore = true)
  MeasurandType toMeasurandType(MeasurandTypeVO measurandTypeVO);
}
