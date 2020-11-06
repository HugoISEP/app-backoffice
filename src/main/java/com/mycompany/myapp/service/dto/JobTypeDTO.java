package com.mycompany.myapp.service.dto;

import com.mycompany.myapp.domain.Entreprise;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.service.view.JobTypeView;


public class JobTypeDTO implements JobTypeView {
    private Long id;
    private String name;
    private Entreprise entreprise;

    public JobTypeDTO() {
    }

    public JobTypeDTO(Long id, String name, Entreprise entreprise) {
        this.id = id;
        this.name = name;
        this.entreprise = entreprise;
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

    public Entreprise getEntreprise() {
        return entreprise;
    }

    public void setEntreprise(Entreprise entreprise) {
        this.entreprise = entreprise;
    }
}
