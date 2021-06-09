package com.mycompany.myapp.service.view;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public interface MissionUserView {
    Long getId();
    String getName();
    String getProjectManagerEmail();
    LocalDateTime getEndedAt();
    CompanyView getCompany();
}
