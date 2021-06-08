package com.mycompany.myapp.service.dto;

import com.mycompany.myapp.service.view.DocumentTypeView;
import lombok.Data;

@Data
public class DocumentTypeDto implements DocumentTypeView {
    Long id;
    String name;
}
