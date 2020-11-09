package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Position;
import com.mycompany.myapp.service.dto.PositionDTO;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PositionMapper {
    PositionDTO asDto(Position in);
    List<PositionDTO> asListDTO(List<Position> in);
    Position fromDTO(PositionDTO in);

    void updatePosition(Position in, @MappingTarget Position out);
}
