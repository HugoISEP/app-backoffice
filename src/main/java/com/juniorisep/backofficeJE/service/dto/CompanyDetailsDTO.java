package com.juniorisep.backofficeJE.service.dto;

import com.juniorisep.backofficeJE.service.view.CompanyDetailsView;
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
