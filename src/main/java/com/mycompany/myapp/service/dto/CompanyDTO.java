package com.mycompany.myapp.service.dto;

import com.mycompany.myapp.service.view.CompanyView;
import lombok.*;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class CompanyDTO extends BasicCompanyDTO implements CompanyView {

    private LocalDateTime createdAt;
    private String emailTemplate;
    private String color;
    private String imagePath;
    private String websiteUrl;

    @Builder
    public CompanyDTO(Long id, String name, LocalDateTime createdAt, String emailTemplate, String color, String imagePath, String websiteUrl){
        super(id, name);
        this.createdAt = createdAt;
        this.emailTemplate = emailTemplate;
        this.color = color;
        this.imagePath = imagePath;
        this.websiteUrl = websiteUrl;
    }

}
