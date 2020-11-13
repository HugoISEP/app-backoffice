package com.mycompany.myapp.service.dto;

import com.mycompany.myapp.service.view.CompanyDetailsView;

public class CompanyDetailsDTO extends CompanyDTO implements CompanyDetailsView {
    private int totalUsers;

    public CompanyDetailsDTO(int totalUsers) {
        this.totalUsers = totalUsers;
    }

    public CompanyDetailsDTO(Long id, String name, String emailTemplate, int totalUsers) {
        super(id, name, emailTemplate);
        this.totalUsers = totalUsers;
    }

    public int getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(int totalUsers) {
        this.totalUsers = totalUsers;
    }
}
