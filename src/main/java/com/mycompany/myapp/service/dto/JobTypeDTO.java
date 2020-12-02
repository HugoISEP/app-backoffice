package com.mycompany.myapp.service.dto;

import com.mycompany.myapp.service.view.JobTypeView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobTypeDTO implements JobTypeView {
    private Long id;
    private String name;
    private CompanyDTO company;
    private Map<String, String> nameTranslations;

}
