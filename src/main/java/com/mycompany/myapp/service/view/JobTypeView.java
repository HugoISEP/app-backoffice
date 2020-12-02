package com.mycompany.myapp.service.view;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public interface JobTypeView {
    Long getId();
    String getName();
    @JsonIgnore
    CompanyView getCompany();
    Map<String, String> getNameTranslations();
}
