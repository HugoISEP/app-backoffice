package com.mycompany.myapp.service.dto;

import com.mycompany.myapp.domain.Document;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocumentDTO {
    private Long id;
    private String type;
    private String fileName;
    private String fileUrl;
}
