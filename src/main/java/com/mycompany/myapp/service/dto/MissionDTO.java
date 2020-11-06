package com.mycompany.myapp.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mycompany.myapp.domain.Entreprise;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.service.view.MissionView;

import java.util.List;

public class MissionDTO implements MissionView {
    private Long id;
    private String name;
    private List<PositionDTO> positions;
    private Entreprise entreprise;

    public MissionDTO(Long id, String name, List<PositionDTO> positions, Entreprise entreprise) {
        this.id = id;
        this.name = name;
        this.positions = positions;
        this.entreprise = entreprise;
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

    public Entreprise getEntreprise() {
        return entreprise;
    }

    public void setEntreprise(Entreprise entreprise) {
        this.entreprise = entreprise;
    }
}
