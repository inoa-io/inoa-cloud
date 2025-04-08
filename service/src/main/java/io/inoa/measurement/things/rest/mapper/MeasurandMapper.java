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

  @Mapping(source = "measurandType.obisId", target = "measurandType")
  MeasurandVO toMeasurandVO(Measurand measurand);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "thing", ignore = true)
  @Mapping(target = "measurandType", ignore = true)
  Measurand toMeasurand(MeasurandVO measurandVO);
}
