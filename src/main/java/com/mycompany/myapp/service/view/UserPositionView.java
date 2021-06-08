package com.mycompany.myapp.service.view;

import java.time.LocalDateTime;

public interface UserPositionView {
    Long getId();
    String getComment();
    Long getRemuneration();
    UserView getUser();
    PositionView getPosition();
    LocalDateTime getEndedAt();
    LocalDateTime getCreatedAt();
}
