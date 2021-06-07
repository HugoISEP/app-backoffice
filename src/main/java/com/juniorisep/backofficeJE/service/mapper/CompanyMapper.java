package com.juniorisep.backofficeJE.service.mapper;

import com.juniorisep.backofficeJE.domain.Company;
import com.juniorisep.backofficeJE.service.dto.CompanyDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CompanyMapper {
    CompanyDTO asDTO(Company in);
    Company fromDTO(CompanyDTO in);
    List<CompanyDTO> asListDTO(List<Company> in);

    void updateCompany(CompanyDTO in, @MappingTarget Company out);
}
