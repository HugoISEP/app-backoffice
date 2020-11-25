package com.mycompany.myapp.service.dto;

import com.mycompany.myapp.domain.Company;
import com.mycompany.myapp.service.view.MissionView;

import java.util.List;

public class MissionDTO implements MissionView {
    private Long id;
    private String name;
    private String projectManagerEmail;
    private List<PositionDTO> positions;
    private Company company;

    public MissionDTO(Long id, String name, String projectManagerEmail, List<PositionDTO> positions, Company company) {
        this.id = id;
        this.name = name;
        this.projectManagerEmail = projectManagerEmail;
        this.positions = positions;
        this.company = company;
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

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public String getProjectManagerEmail() {
        return projectManagerEmail;
    }

    public void setProjectManagerEmail(String projectManagerEmail) {
        this.projectManagerEmail = projectManagerEmail;
    }
}
