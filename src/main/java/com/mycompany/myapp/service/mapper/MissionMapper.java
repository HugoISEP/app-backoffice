package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Mission;
import com.mycompany.myapp.service.dto.MissionDTO;
import org.mapstruct.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MissionMapper {
    Mission fromDTO(MissionDTO in);
    MissionDTO asDTO(Mission in);
    List<MissionDTO> asListDTO(List<Mission> in);

    void updateMission(Mission in, @MappingTarget Mission out);

    @AfterMapping
    default void removeDeletedPositions(@MappingTarget MissionDTO missionDTO) {
        missionDTO.setPositions(missionDTO.getPositions().stream().filter(positionDTO -> Objects.isNull(positionDTO.getDeletedAt())).collect(Collectors.toList()));
    }

    @AfterMapping
    default void removeDeletedListPositions(@MappingTarget List<MissionDTO> in) {
        in.forEach(missionDTO -> missionDTO.setPositions(missionDTO.getPositions().stream().filter(positionDTO -> Objects.isNull(positionDTO.getDeletedAt())).collect(Collectors.toList())));
    }
}
