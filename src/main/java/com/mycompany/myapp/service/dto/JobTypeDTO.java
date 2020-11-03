package com.mycompany.myapp.service.dto;

import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.service.view.JobTypeView;


public class JobTypeDTO implements JobTypeView {
    private Long id;
    private String name;
    private User user;

    public JobTypeDTO() {
    }

    public JobTypeDTO(Long id, String name, User user) {
        this.id = id;
        this.name = name;
        this.user = user;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
