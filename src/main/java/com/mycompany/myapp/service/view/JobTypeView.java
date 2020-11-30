package com.mycompany.myapp.service.view;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.mycompany.myapp.domain.Company;

@JsonInclude(JsonInclude.Include.NON_NULL)
public interface JobTypeView {
    Long getId();
    String getName();
    @JsonIgnore
    CompanyView getCompany();
}
