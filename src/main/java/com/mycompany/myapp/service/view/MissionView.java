package com.mycompany.myapp.service.view;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.mycompany.myapp.domain.Company;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public interface MissionView {
    Long getId();
    String getName();
    String getProjectManagerEmail();
    List<? extends PositionView> getPositions();
    @JsonIgnore
    Company getCompany();
}
