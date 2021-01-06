package com.mycompany.myapp.service.dto;

import com.mycompany.myapp.service.view.JobTypeView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobTypeDTO implements JobTypeView {
    private Long id;
    private String name;
    private String icon;
    private LocalDateTime createdAt;
    private CompanyDTO company;

}
