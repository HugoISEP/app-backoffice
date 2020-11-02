package com.mycompany.myapp.service.dto;

import com.mycompany.myapp.service.view.JobTypeView;


public class JobTypeDTO implements JobTypeView {
    private Long id;
    private String name;

    public JobTypeDTO() {
    }

    public JobTypeDTO(Long id, String name) {
        this.id = id;
        this.name = name;
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
}
