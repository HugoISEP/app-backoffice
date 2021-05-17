package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.UserPosition;
import com.mycompany.myapp.service.dto.UserPositionDto;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserPositionMapper {
    UserPositionDto asDto(UserPosition in);
    UserPosition fromDto(UserPositionDto in);
}
