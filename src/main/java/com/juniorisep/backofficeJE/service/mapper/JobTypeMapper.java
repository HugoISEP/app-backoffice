package com.juniorisep.backofficeJE.service.mapper;

import com.juniorisep.backofficeJE.service.dto.JobTypeDTO;
import com.juniorisep.backofficeJE.domain.JobType;
import org.mapstruct.Mapper;
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
