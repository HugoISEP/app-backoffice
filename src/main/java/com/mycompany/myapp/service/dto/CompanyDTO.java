package com.mycompany.myapp.service.dto;

import com.mycompany.myapp.service.view.CompanyView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompanyDTO implements CompanyView {

    private Long id;
    private String name;
    private LocalDateTime createdAt;
    private String emailTemplate;
    private String color;
    private String imagePath;

}
