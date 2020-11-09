package com.mycompany.myapp.service.dto;

import com.mycompany.myapp.service.view.CompanyView;

import java.util.Objects;

public class CompanyDTO implements CompanyView {

    private Long id;
    private String name;
    private String emailTemplate;

    public CompanyDTO() {
    }

    public CompanyDTO(Long id, String name, String emailTemplate) {
        this.id = id;
        this.name = name;
        this.emailTemplate = emailTemplate;
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

    public String getEmailTemplate() {
        return emailTemplate;
    }

    public void setEmailTemplate(String emailTemplate) {
        this.emailTemplate = emailTemplate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompanyDTO that = (CompanyDTO) o;
        return Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(emailTemplate, that.emailTemplate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, emailTemplate);
    }
}
