package com.mycompany.myapp.service.view;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.mycompany.myapp.domain.Company;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public interface JobTypeView {
    Long getId();
    String getName();
    String getIcon();
    LocalDateTime getCreatedAt();
    @JsonIgnore
    CompanyView getCompany();
}
