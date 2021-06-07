package com.juniorisep.backofficeJE.service.dto;

import com.juniorisep.backofficeJE.service.view.JobTypeView;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
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
