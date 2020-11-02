package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.JobType;
import com.mycompany.myapp.service.dto.JobTypeDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface JobTypeMapper {
    JobTypeDTO asDTO(JobType in);
    List<JobTypeDTO> asListDTO(List<JobType> in);
    JobType fromDTO(JobTypeDTO in);
}
