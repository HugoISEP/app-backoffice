package com.mycompany.myapp.service.dto;

import com.mycompany.myapp.service.view.BasicCompanyView;
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
