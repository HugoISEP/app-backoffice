package com.mycompany.myapp.service.view;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public interface CompanyView extends BasicCompanyView {
    LocalDateTime getCreatedAt();
    String getEmailTemplate();
    String getColor();
    String getImagePath();
    String getWebsiteUrl();
}
