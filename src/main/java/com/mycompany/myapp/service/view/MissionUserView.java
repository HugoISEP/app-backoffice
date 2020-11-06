package com.mycompany.myapp.service.view;

import com.mycompany.myapp.domain.Entreprise;
import com.mycompany.myapp.domain.User;

public interface MissionUserView {
    Long getId();
    String getName();
    Entreprise getEntreprise();
}
