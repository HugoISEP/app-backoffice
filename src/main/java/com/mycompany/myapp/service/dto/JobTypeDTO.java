package com.mycompany.myapp.service.dto;

import com.mycompany.myapp.domain.Company;
import com.mycompany.myapp.service.view.JobTypeView;


public class JobTypeDTO implements JobTypeView {
    private Long id;
    private String name;
    private Company company;

    public JobTypeDTO() {
    }

    public JobTypeDTO(Long id, String name, Company company) {
        this.id = id;
        this.name = name;
        this.company = company;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }
}
