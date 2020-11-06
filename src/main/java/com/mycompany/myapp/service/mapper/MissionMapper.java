package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.JobType;
import com.mycompany.myapp.domain.Mission;
import com.mycompany.myapp.service.dto.MissionDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MissionMapper {
    Mission fromDTO(MissionDTO in);
    MissionDTO asDTO(Mission in);
    List<MissionDTO> asListDTO(List<Mission> in);

    @Mapping(target = "entreprise", ignore = true)
    void updateMission(Mission in, @MappingTarget Mission out);

}
