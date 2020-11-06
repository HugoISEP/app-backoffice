package com.mycompany.myapp.service.dto;

import com.mycompany.myapp.domain.Company;
import com.mycompany.myapp.service.view.MissionUserView;

public class MissionUserDTO implements MissionUserView {
    private Long id;
    private String name;
    private Company company;

    public MissionUserDTO() {
    }

    public MissionUserDTO(Long id, String name, Company company) {
        this.id = id;
        this.name = name;
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
    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }
}
