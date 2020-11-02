package com.mycompany.myapp.service.dto;

import com.mycompany.myapp.service.view.MissionView;

import java.util.List;

public class MissionDTO implements MissionView {
    private Long id;
    private String name;
    private List<PositionDTO> positions;

    public MissionDTO(Long id, String name, List<PositionDTO> positions) {
        this.id = id;
        this.name = name;
        this.positions = positions;
    }

    public MissionDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<PositionDTO> getPositions() {
        return positions;
    }

    public void setPositions(List<PositionDTO> positions) {
        this.positions = positions;
    }
}
