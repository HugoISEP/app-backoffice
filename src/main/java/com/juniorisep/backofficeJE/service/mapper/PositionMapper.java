package com.juniorisep.backofficeJE.service.mapper;

import com.juniorisep.backofficeJE.domain.Position;
import com.juniorisep.backofficeJE.service.dto.PositionDTO;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PositionMapper {
    PositionDTO asDto(Position in);
    List<PositionDTO> asListDTO(List<Position> in);
    Position fromDTO(PositionDTO in);

    void updatePosition(Position in, @MappingTarget Position out);
}
