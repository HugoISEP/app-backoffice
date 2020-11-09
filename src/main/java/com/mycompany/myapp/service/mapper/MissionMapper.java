package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Mission;
import com.mycompany.myapp.service.dto.MissionDTO;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MissionMapper {
    Mission fromDTO(MissionDTO in);
    MissionDTO asDTO(Mission in);
    List<MissionDTO> asListDTO(List<Mission> in);

    void updateMission(Mission in, @MappingTarget Mission out);

}
