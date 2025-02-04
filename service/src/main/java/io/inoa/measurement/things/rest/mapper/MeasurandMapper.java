package io.inoa.measurement.things.rest.mapper;

import io.inoa.measurement.things.domain.Measurand;
import io.inoa.rest.MeasurandVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(
    uses = {MeasurandTypeMapper.class},
    componentModel = MappingConstants.ComponentModel.JAKARTA)
public interface MeasurandMapper {

  @Mapping(target = "uri", ignore = true)
  MeasurandVO toMeasurandVO(Measurand measurand);
}
