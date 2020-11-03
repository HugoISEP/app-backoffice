package com.mycompany.myapp.service.view;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;

public interface PositionView {
    Long getId();
    int getDuration();
    String getDescription();
    LocalDateTime getCreatedAt();
    boolean isStatus();
    JobTypeView getJobType();
    @JsonIgnore
    MissionUserView getMission();
}
