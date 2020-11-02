package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Position;
import com.mycompany.myapp.service.dto.PositionDTO;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring",nullValueMappingStrategy= NullValueMappingStrategy.RETURN_DEFAULT)
public interface PositionMapper {
    PositionDTO asDto(Position in);
    List<PositionDTO> asListDTO(List<Position> in);
    Position fromDTO(PositionDTO in);
}
