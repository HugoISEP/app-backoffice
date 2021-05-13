package com.mycompany.myapp.service.view;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public interface PositionView {
    Long getId();
    int getDuration();
    int getPlacesNumber();
    Float getRemuneration();
    String getDescription();
    LocalDateTime getCreatedAt();
    boolean isStatus();
    JobTypeView getJobType();
    MissionUserView getMission();
    LocalDateTime getDeletedAt();
}
