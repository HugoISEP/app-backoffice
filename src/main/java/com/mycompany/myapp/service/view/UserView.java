package com.mycompany.myapp.service.view;

import com.mycompany.myapp.domain.Company;
import com.mycompany.myapp.domain.JobType;

import java.time.Instant;
import java.util.List;
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
    Company getCompany();
    List<JobType> getJobTypes();
}
