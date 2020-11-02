package com.mycompany.myapp.service.view;

import java.util.List;

public interface MissionView {
    Long getId();
    String getName();
    List<? extends PositionView> getPositions();
}
