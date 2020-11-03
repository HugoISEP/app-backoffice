package com.mycompany.myapp.service.dto;

import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.service.view.MissionUserView;

public class MissionUserDTO implements MissionUserView {
    private Long id;
    private String name;
    private User user;

    public MissionUserDTO() {
    }

    public MissionUserDTO(Long id, String name, User user) {
        this.id = id;
        this.name = name;
        this.user = user;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
