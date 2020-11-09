package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Company;
import com.mycompany.myapp.service.dto.CompanyDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CompanyMapper {
    CompanyDTO asDTO(Company in);
    Company fromDTO(CompanyDTO in);
    List<CompanyDTO> asListDTO(List<Company> in);

    void updateCompany(CompanyDTO in, @MappingTarget Company out);
}
