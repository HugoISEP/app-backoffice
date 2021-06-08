package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Document;
import com.mycompany.myapp.domain.DocumentType;
import com.mycompany.myapp.service.MinioService;
import com.mycompany.myapp.service.dto.DocumentDTO;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, uses = MinioService.class)
public interface DocumentMapper {
    @Mapping(source = "d.type", target = "type", qualifiedByName = "typeMapper")
    @Mapping(source = "d.filePath", target = "fileName")
    @Mapping(source = "d", target = "fileUrl", qualifiedByName = "getFileUrl")
    DocumentDTO asDTO(Document d);

    @Mapping(target = "documentType", ignore = true)
    List<DocumentDTO> asListDTO(List<Document> d);


    @Named("typeMapper")
    default String typeMapper(DocumentType type) {
        return type.getName();
    }
}
