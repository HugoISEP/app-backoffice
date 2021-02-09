package com.mycompany.myapp.service.dto;

import com.mycompany.myapp.service.view.CompanyDetailsView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class CompanyDetailsDTO extends CompanyDTO implements CompanyDetailsView {
    private int totalUsers;
}
