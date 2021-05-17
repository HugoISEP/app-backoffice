package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.UserPosition;
import com.mycompany.myapp.service.dto.UserPositionDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserPositionMapper {

    @Mapping(target = "user", ignore = true)
    UserPositionDto asDto(UserPosition in);

    @Mapping(target = "user", ignore = true)
    UserPosition fromDto(UserPositionDto in);
}
