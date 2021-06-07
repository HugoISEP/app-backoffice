package com.juniorisep.backofficeJE.service.dto;

import com.juniorisep.backofficeJE.service.view.BasicCompanyView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BasicCompanyDTO implements BasicCompanyView {
    private Long id;
    private String name;
}
