package com.mycompany.myapp.service.view;

import java.time.LocalDateTime;

public interface PositionView {
    Long getId();
    int getDuration();
    String getDescription();
    LocalDateTime getCreatedAt();
    boolean isStatus();
    JobTypeView getJobType();
}
