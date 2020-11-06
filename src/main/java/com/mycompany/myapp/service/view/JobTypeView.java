package com.mycompany.myapp.service.view;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mycompany.myapp.domain.Entreprise;

public interface JobTypeView {
    Long getId();
    String getName();
    @JsonIgnore
    Entreprise getEntreprise();
}
