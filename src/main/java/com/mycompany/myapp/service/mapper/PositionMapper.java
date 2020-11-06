package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Position;
import com.mycompany.myapp.service.dto.PositionDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PositionMapper {
    PositionDTO asDto(Position in);
    List<PositionDTO> asListDTO(List<Position> in);
    Position fromDTO(PositionDTO in);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "mission", ignore = true)
    void updatePosition(Position in, @MappingTarget Position out);
}
