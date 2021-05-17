package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Document;
import com.mycompany.myapp.service.dto.DocumentDTO;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DocumentMapper {
    DocumentDTO asDTO(Document d);
    List<DocumentDTO> asListDTO(List<Document> d);
}
