package com.juniorisep.backofficeJE.service.view;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

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
