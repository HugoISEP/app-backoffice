package com.mycompany.myapp.service.view;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mycompany.myapp.domain.Company;

@JsonInclude(JsonInclude.Include.NON_NULL)
public interface MissionUserView {
    Long getId();
    String getName();
    Company getCompany();
}
