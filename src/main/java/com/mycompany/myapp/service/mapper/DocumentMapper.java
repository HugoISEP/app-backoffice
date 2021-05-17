package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Document;
import com.mycompany.myapp.service.dto.DocumentDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DocumentMapper {
    @Mapping(target = "type", ignore = true)
    DocumentDTO asDTO(Document d);

    @Mapping(target = "documentType", ignore = true)
    List<DocumentDTO> asListDTO(List<Document> d);
}
