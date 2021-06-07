package com.juniorisep.backofficeJE.service.dto;

import com.juniorisep.backofficeJE.service.view.MissionUserView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MissionUserDTO implements MissionUserView {
    private Long id;
    private String name;
    private String projectManagerEmail;
    private CompanyDTO company;

}
