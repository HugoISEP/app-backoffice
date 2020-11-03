package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Mission;
import com.mycompany.myapp.service.dto.MissionDTO;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MissionMapper {
    Mission fromDTO(MissionDTO in);
    MissionDTO asDTO(Mission in);
    List<MissionDTO> asListDTO(List<Mission> in);
}
