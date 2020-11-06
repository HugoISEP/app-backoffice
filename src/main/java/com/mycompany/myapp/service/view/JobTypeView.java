package com.mycompany.myapp.service.view;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mycompany.myapp.domain.Company;

public interface JobTypeView {
    Long getId();
    String getName();
    @JsonIgnore
    Company getCompany();
}
