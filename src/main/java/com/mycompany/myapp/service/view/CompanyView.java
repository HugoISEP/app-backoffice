package com.mycompany.myapp.service.view;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public interface CompanyView {
    Long getId();
    String getName();
    LocalDateTime getCreatedAt();
    String getEmailTemplate();
    String getColor();
    String getWebsiteUrl();
}
