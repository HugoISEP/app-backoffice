package com.mycompany.myapp.service.view;

import java.time.LocalDateTime;

public interface UserPositionView {
    Long getId();
    String getComment();
    Long getRemuneration();
    String getUserLogin();
    PositionView getPosition();
    LocalDateTime getEndedAt();
    LocalDateTime getCreatedAt();
    Long getMark();
}
