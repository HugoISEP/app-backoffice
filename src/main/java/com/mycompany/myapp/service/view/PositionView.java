package com.mycompany.myapp.service.view;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public interface PositionView {
    Long getId();
    int getDuration();
    int getPlacesNumber();
    float getRemuneration();
    String getDescription();
    Map<String, String> getDescriptionTranslations();
    LocalDateTime getCreatedAt();
    boolean isStatus();
    JobTypeView getJobType();
    @JsonIgnore
    MissionUserView getMission();
}
