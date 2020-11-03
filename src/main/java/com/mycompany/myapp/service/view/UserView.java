package com.mycompany.myapp.service.view;

import java.time.Instant;
import java.util.Set;

public interface UserView {

    Long getId();
    String getLogin();
    String getFirstName();
    String getLastName();
    String getEmail();
    String getImageUrl();
    boolean isActivated();
    String getLangKey();
    String getCreatedBy();
    Instant getCreatedDate();
    String getLastModifiedBy();
    Instant getLastModifiedDate();
    Set<String> getAuthorities();
}
