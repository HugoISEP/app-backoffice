package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Company;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CompanyMapper {
    void updateCompany(Company in, @MappingTarget Company out);
}
