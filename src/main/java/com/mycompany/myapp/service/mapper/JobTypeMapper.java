package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.JobType;
import com.mycompany.myapp.service.dto.JobTypeDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface JobTypeMapper {
    JobTypeDTO asDTO(JobType in);
    List<JobTypeDTO> asListDTO(List<JobType> in);
    JobType fromDTO(JobTypeDTO in);

    void updateJobtype(JobType in, @MappingTarget JobType out);
}
