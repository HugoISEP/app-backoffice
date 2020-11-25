package com.mycompany.myapp.service.dto;

import com.mycompany.myapp.domain.Company;
import com.mycompany.myapp.service.view.MissionUserView;

public class MissionUserDTO implements MissionUserView {
    private Long id;
    private String name;
    private String projectManagerEmail;
    private CompanyDTO company;

    public MissionUserDTO() {
    }

    public MissionUserDTO(Long id, String name, String projectManagerEmail, CompanyDTO company) {
        this.id = id;
        this.name = name;
        this.projectManagerEmail = projectManagerEmail;
        this.company = company;
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

    @Override
    public CompanyDTO getCompany() {
        return company;
    }

    public void setCompany(CompanyDTO company) {
        this.company = company;
    }

    @Override
    public String getProjectManagerEmail() {
        return projectManagerEmail;
    }

    public void setProjectManagerEmail(String projectManagerEmail) {
        this.projectManagerEmail = projectManagerEmail;
    }
}
