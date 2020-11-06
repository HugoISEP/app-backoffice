package com.mycompany.myapp.service.dto;

import com.mycompany.myapp.domain.Entreprise;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.service.view.MissionUserView;

public class MissionUserDTO implements MissionUserView {
    private Long id;
    private String name;
    private Entreprise entreprise;

    public MissionUserDTO() {
    }

    public MissionUserDTO(Long id, String name, Entreprise entreprise) {
        this.id = id;
        this.name = name;
        this.entreprise = entreprise;
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
    public Entreprise getEntreprise() {
        return entreprise;
    }

    public void setEntreprise(Entreprise entreprise) {
        this.entreprise = entreprise;
    }
}
