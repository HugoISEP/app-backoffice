package com.mycompany.myapp.service.dto;

import com.mycompany.myapp.domain.Company;
import com.mycompany.myapp.service.view.JobTypeView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobTypeDTO implements JobTypeView {
    private Long id;
    private String name;
    private Company company;

}
