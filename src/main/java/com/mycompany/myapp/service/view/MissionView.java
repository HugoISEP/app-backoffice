package com.mycompany.myapp.service.view;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mycompany.myapp.domain.Company;

import java.util.List;

public interface MissionView {
    Long getId();
    String getName();
    List<? extends PositionView> getPositions();
    @JsonIgnore
    Company getCompany();
}
