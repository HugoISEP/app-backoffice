package com.mycompany.myapp.service.view;

public interface UserPositionView {
    Long getId();
    String getComment();
    Long getRemuneration();
    UserView getUser();
    PositionView getPosition();
}
